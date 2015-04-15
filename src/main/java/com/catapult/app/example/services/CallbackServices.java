package com.catapult.app.example.services;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bandwidth.sdk.model.Bridge;
import com.bandwidth.sdk.model.Call;
import com.bandwidth.sdk.model.events.AnswerEvent;
import com.bandwidth.sdk.model.events.Event;
import com.bandwidth.sdk.model.events.EventBase;
import com.bandwidth.sdk.model.events.HangupEvent;
import com.bandwidth.sdk.model.events.IncomingCallEvent;
import com.catapult.app.example.adapters.CallbackAdapter;
import com.catapult.app.example.beans.CallEvents;
import com.catapult.app.example.beans.User;
import com.catapult.app.example.exceptions.UserNotFoundException;
import com.catapult.app.example.util.URLUtil;

@Service
public class CallbackServices {

    private static final Logger LOG = Logger.getLogger(CallbackServices.class);

    private static final Pattern sipPattern = Pattern.compile("^sip:.+@.+");

    /**
     * Store the call legs participating in the bridge
     */
    private static final Map<String, String> bridgeMap = new ConcurrentHashMap<>();

    /**
     * Store the incoming and outgoing call events
     */
    private static final Map<String, Map<String, CallEvents>> userEventCallMap = new ConcurrentHashMap<>();

    @Autowired
    private UserServices userServices;

    public void handleIncomingCallback(final String eventString, final String userName, final String baseAppUrl) {
        try {
            Event event = EventBase.createEventFromString(eventString);
            if (event instanceof IncomingCallEvent) {

                if (event.getProperty("from") != null && sipPattern.matcher(event.getProperty("from")).find()) {
                    // Case Incoming call from Endpoint to a PSTN number
                    createCallFromEndpoint(event, userName, baseAppUrl);

                } else if (event.getProperty("to") != null) {
                    // Case Incoming call from PSTN to a number associated to an Endpoint
                    createCallToEndpoint(event, userName, baseAppUrl);
                }
            } else if (event instanceof HangupEvent) {
                checkCallHangup(event, userName);

            } else {
                addCallEvent(event, userName);
            }
        } catch (Exception e) {
            LOG.error("Could not create incoming leg event from string", e);
        }
    }

    public void handleOutgoingCallback(final String eventString, final String userName, final String baseAppUrl) {
        try {
            Event event = EventBase.createEventFromString(eventString);

            if (event instanceof AnswerEvent) {
                bridgeCalls(event, userName);

            } else if (event instanceof HangupEvent) {
                checkCallHangup(event, userName);

            } else {
                addCallEvent(event, userName);
            }
        } catch (Exception e) {
            LOG.error("Could not create outgoing leg event from string", e);
        }
    }

    private void createCallFromEndpoint(final Event event, final String userName, final String baseAppUrl) {
        try {
            User user = userServices.getUser(userName);

            if (user.getEndpoint().getSipUri().contains(event.getProperty("from").trim())) {
                createCall(event, userName, event.getProperty("to"), user.getPhoneNumber(), baseAppUrl);
            } else {
                LOG.error(MessageFormat.format("Could not find the endpoint [{0}] for userName [{1}]",
                        event.getProperty("from"), userName));
            }
        } catch (UserNotFoundException e) {
            LOG.error("User not found to create call", e);

        } catch (Exception e) {
            LOG.error("Could not create outbound call based on call FROM Endpoint", e);
        }
    }

    private void createCallToEndpoint(final Event event, final String userName, final String baseAppUrl) {
        try {
            User user = userServices.getUser(userName);

            if (hasActiveCall(event)) {
                LOG.info(MessageFormat.format("Call to [{0}] already exist. Doing nothing with call [{0}] to [{1}]",
                        event.getProperty("callId"), event.getProperty("to")));
                return;
            }

            if (user.getPhoneNumber().contains(event.getProperty("to").trim())) {
                createCall(event, userName, user.getEndpoint().getSipUri(), event.getProperty("to"), baseAppUrl);
            } else {
                LOG.error(MessageFormat.format("Could not find the number [{0}] for userName [{1}]",
                        event.getProperty("to"), userName));
            }
        } catch (UserNotFoundException e) {
            LOG.error("User not found to create call", e);

        } catch (Exception e) {
            LOG.error("Could not create outbound call based on call TO Endpoint", e);
        }
    }

    private void createCall(final Event event, final String userName, final String to, final String from,
                            final String baseAppUrl) throws UserNotFoundException, Exception {

        User user = userServices.getUser(userName);

        // Create outgoing call using Bandwidth SDK
        Call call = Call.create(to, from, URLUtil.getOutgoingCallbackUrl(baseAppUrl, userName), null);

        if (call == null || call.getId() == null) {
            LOG.error(MessageFormat.format("Could not create outgoing call for incoming call [{0}]",
                    event.getProperty("callId")));
            return;
        }

        LOG.info(MessageFormat.format("Created outgoing call [{0}] for incomingCall [{1}]",
                call.getId(), event.getProperty("callId")));

        Map<String, CallEvents> callEventMap = userEventCallMap.get(userName);
        if (callEventMap == null) {
            callEventMap = new ConcurrentHashMap<>();
            userEventCallMap.put(userName, callEventMap);
        }

        // The events for outgoing call will be saved after AnswerEvent
        CallEvents outgoingCall = new CallEvents(call.getId());

        // Save the incoming call event
        CallEvents incomingCall = new CallEvents(event.getProperty("callId"));
        incomingCall.addEvent(event);

        // Keep track of each call events
        callEventMap.put(outgoingCall.getCallId(), outgoingCall);
        callEventMap.put(incomingCall.getCallId(), incomingCall);

        // Keep track of call ids to create a bridge
        bridgeMap.put(outgoingCall.getCallId(), incomingCall.getCallId());
        bridgeMap.put(incomingCall.getCallId(), outgoingCall.getCallId());
    }

    private void bridgeCalls(final Event event, final String userName) {
        if (userName == null) {
            LOG.error(MessageFormat.format("Could not find the username for call [{0}]",
                    event.getProperty("callId")));
            return;
        }

        String secondCallId = bridgeMap.get(event.getProperty("callId"));
        if (secondCallId == null) {
            LOG.error(MessageFormat.format("No calls mapped to create bridge for user [{0}] based on call [{1}]",
                    userName, event.getProperty("callId")));
            return;
        }

        Map<String, CallEvents> callEventMap = userEventCallMap.get(userName);
        if (callEventMap == null) {
            LOG.error(MessageFormat.format("No call mapped for user [{0}]", userName));
            return;
        }

        // Add event when call is answered
        CallEvents callEvents = callEventMap.get(event.getProperty("callId"));
        callEvents.addEvent(event);

        try {
            // Bridge the incoming and outgoing calls using Bandwidth SDK
            Bridge bridge = Bridge.create(secondCallId, event.getProperty("callId"));

            if (bridge == null || bridge.getId() == null) {
                LOG.error(MessageFormat.format("Could not bridge calls [{0}, {1}]",
                        secondCallId, event.getProperty("callId")));
            }

            LOG.info(MessageFormat.format("Calls [{0}, {1}] successfully bridged by [{2}]",
                    secondCallId, event.getProperty("callId"), bridge.getId()));

        } catch (Exception e) {
            LOG.error(MessageFormat.format("Bridge could not be created for calls [{0}, {1}]",
                    secondCallId, event.getProperty("callId")), e);
        }
    }

    private void addCallEvent(final Event event, final String userName) {
        if (userName == null) {
            LOG.error(MessageFormat.format("Could not find the username for call [{0}]",
                    event.getProperty("callId")));
            return;
        }

        Map<String, CallEvents> callEventMap = userEventCallMap.get(userName);
        if (callEventMap == null) {
            LOG.error(MessageFormat.format("No call mapped for user [{0}]", userName));
            return;
        }

        if (event.getProperty("callId") == null) {
            LOG.error(MessageFormat.format("Could not find the callId on event [{0}]", event));
            return;
        }

        // Add call event
        CallEvents callEvents = callEventMap.get(event.getProperty("callId"));
        if (callEvents != null) {
            callEvents.addEvent(event);
        } else {
            LOG.error(MessageFormat.format("Could not find the call events mapped for userName [{0}] " +
                    "and call [{1}]", userName, event.getProperty("callId")));
        }
    }

    private void checkCallHangup(final Event event, final String userName) {
        if (userName == null) {
            LOG.error(MessageFormat.format("Could not find the username for call [{0}]",
                    event.getProperty("callId")));
            return;
        }

        Map<String, CallEvents> callEventMap = userEventCallMap.get(userName);
        if (callEventMap == null) {
            LOG.error(MessageFormat.format("No call mapped for user [{0}]", userName));
            return;
        }

        if (event.getProperty("callId") == null) {
            LOG.error(MessageFormat.format("Could not find the callId on event [{0}]", event));
            return;
        }

        String secondCallId = bridgeMap.get(event.getProperty("callId"));
        if (secondCallId == null) {
            LOG.error(MessageFormat.format("No calls mapped to hangup second call leg for user [{0}] " +
                            "based on call [{1}]", userName, event.getProperty("callId")));
            return;
        }

        // Add call event
        CallEvents callEvents = callEventMap.get(event.getProperty("callId"));
        if (callEvents != null) {
            callEvents.addEvent(event);
        } else {
            LOG.error(MessageFormat.format("Could not find the call events mapped for userName [{0}] " +
                            "and call [{1}]", userName, event.getProperty("callId")));
        }

        // Need to synchronize on callEventMap per user to avoid
        // connection allocation problem when calling the api
        synchronized (callEventMap) {

            // Hangup second call leg
            hangupCall(secondCallId);
        }
    }

    private void hangupCall(final String callId) {
        try {
            Call call = Call.get(callId);

            if (call != null) {
                call.hangUp();
            } else {
                LOG.error(MessageFormat.format("Could not find call [{0}] to hangup", callId));
            }
        } catch (Exception e) {
            LOG.error(MessageFormat.format("Error while getting call [{0}]", callId), e);
        }
    }

    private boolean hasActiveCall(final Event event) {
        for (Map<String, CallEvents> callEventMap : userEventCallMap.values()) {

            for (CallEvents callEvents : callEventMap.values()) {
                if (callEvents.hasActiveCall(event)) {
                    return true;
                }
            }
        }
        return false;
    }

    public List<CallbackAdapter> getUserCallbacks(final String userName) {
        return getUserCallbacks(userName, null);
    }

    public List<CallbackAdapter> getUserCallbacks(final String userName, final String callId) {
        Map<String, CallEvents> callEventMap = userEventCallMap.get(userName);
        if (callEventMap == null) {
            return Collections.emptyList();
        }

        List<CallbackAdapter> userEvents = new ArrayList<CallbackAdapter>();

        if (callId != null) {
            CallEvents callEvents = callEventMap.get(callId);
            userEvents.add(new CallbackAdapter(callEvents));
            return userEvents;
        }

        for (CallEvents callEvents : callEventMap.values()) {
            userEvents.add(new CallbackAdapter(callEvents));
        }
        return userEvents;
    }

}