package com.catapult.app.example.services;

import java.text.MessageFormat;
import java.util.*;
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

    public synchronized void handleIncomingCallback(final String eventString, final String userName, final String baseAppUrl) {
        try {
            Event event = EventBase.createEventFromString(eventString);
            if (event instanceof IncomingCallEvent) {
                // Answer the incoming call
                Call incomingCall = answerIncomingCall(event, userName);

                Map<String, CallEvents> callEventMap = userEventCallMap.get(userName);
                if (callEventMap == null) {
                    callEventMap = new ConcurrentHashMap<>();
                    userEventCallMap.put(userName, callEventMap);
                }

                // Save the incoming call event
                CallEvents incomingCallEvents = new CallEvents(incomingCall.getId());
                incomingCallEvents.addEvent(event);


            } else if (event instanceof AnswerEvent) {

                //if (event.getProperty("tag") == null) {
                    // This is an answer for the inbound leg
                    LOG.info(MessageFormat.format("Answered INCOMING call leg [{0}]", ((AnswerEvent) event).getId()));

                    String to = event.getProperty("to");
                    String from = event.getProperty("from");
                    User user = userServices.getUser(userName);

                    if (event.getProperty("from") != null && sipPattern.matcher(event.getProperty("from")).find()) {
                        // Case Incoming call from Endpoint to a PSTN number
                        from = user.getPhoneNumber();

                    } else if (event.getProperty("to") != null) {
                        // Case Incoming call from PSTN to a number associated to an Endpoint
                        to = user.getEndpoint().getSipUri();
                    }

                    // Add it to a new bridge
                    Bridge bridge = addIncomingCallToBridge(event, userName);

                    Call incomingCall = Call.get(event.getProperty("callId"));

                    // Play ringing for the caller while they're waiting
                    playRinging(baseAppUrl, incomingCall);

                    // Create the outbound leg of the call on the new bridge
                    Call outgoingCall = createCall(event, userName, to, from, bridge.getId(), baseAppUrl);

                    Map<String, CallEvents> callEventMap = userEventCallMap.get(userName);
                    if (callEventMap == null) {
                        callEventMap = new ConcurrentHashMap<>();
                        userEventCallMap.put(userName, callEventMap);
                    }

                    // The events for outgoing call will be saved after AnswerEvent
                    CallEvents outgoingCallEvents = new CallEvents(outgoingCall.getId());


                    // Keep track of each call events
                    callEventMap.put(outgoingCall.getId(), outgoingCallEvents);

                    // Add the call legs to the bridge map
                    bridgeMap.put(outgoingCall.getId(), incomingCall.getId());
                    bridgeMap.put(incomingCall.getId(), outgoingCall.getId());

                //}


            } else if (event instanceof HangupEvent) {
                checkCallHangup(event, userName);

            } else {
                addCallEvent(event, userName);
            }
        } catch (Exception e) {
            LOG.error("Could not create incoming leg event from string", e);
        }
    }

    public synchronized void handleOutgoingCallback(final String eventString, final String userName, final String baseAppUrl) {
        try {
            Event event = EventBase.createEventFromString(eventString);

            if (event instanceof AnswerEvent) {
                // This is the answer event from the user's endpoint
                Map<String, CallEvents> callEventMap = userEventCallMap.get(userName);
                if (callEventMap == null) {
                    callEventMap = new ConcurrentHashMap<>();
                    userEventCallMap.put(userName, callEventMap);
                }

                // The events for outgoing call will be saved after AnswerEvent
                CallEvents outgoingCallEvents = new CallEvents(event.getProperty("callId"));


                // Keep track of each call events
                callEventMap.put(event.getProperty("callId"), outgoingCallEvents);

            } else if (event instanceof HangupEvent) {
                checkCallHangup(event, userName);

            } else {
                addCallEvent(event, userName);
            }
        } catch (Exception e) {
            LOG.error("Could not create outgoing leg event from string", e);
        }
    }

    private Call answerIncomingCall(final Event event, final String userName) throws Exception {
        final String callId = event.getProperty("callId");
        Call call = Call.get(callId);
        LOG.info(MessageFormat.format("Answering INCOMING LEG [{0}]", callId));
        call.answerOnIncoming();
        return call;
    }

    private Bridge addIncomingCallToBridge(final Event event, final String userName) throws Exception {
        final String incomingCallId = event.getProperty("callId");

        if (userName == null) {
            throw new Exception(MessageFormat.format("Could not find the username for call [{0}]", incomingCallId));
        }

        // Bridge the incoming and outgoing calls using Bandwidth SDK
        Bridge bridge = Bridge.create(incomingCallId);

        if (bridge == null || bridge.getId() == null) {
            throw new Exception(MessageFormat.format("Could not add call [{0}] to bridge",
                    incomingCallId));
        }

        LOG.info(MessageFormat.format("Call [{0}] added to bridge [{1}]",
                incomingCallId, bridge.getId()));

        return bridge;
    }

    private void playRinging(String baseAppUrl, Call call) throws Exception {
        String url = URLUtil.getStaticResourceUrl(baseAppUrl, "sounds", "ring.mp3");

        HashMap<String, Object> params = new HashMap<>();
        params.put("fileUrl", url);
        params.put("loopEnabled", true);

        call.playAudio(params);
    }

    private Call createCall(final Event event, final String userName, final String to, final String from, final String bridgeId, String baseAppUrl) throws Exception {
        HashMap<String, Object> params = new HashMap<>();
        params.put("to", to);
        params.put("from", from);
        params.put("tag", event.getProperty("callId"));
        params.put("callbackUrl", URLUtil.getOutgoingCallbackUrl(baseAppUrl, userName));
        if (bridgeId != null) params.put("bridgeId", bridgeId);
        Call call = Call.create(params);
        LOG.info(MessageFormat.format("Created OUTGOING LEG [{0}]", call.getId()));
        return call;
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

        // Hangup second call leg
        hangupCall(secondCallId);
    }

    // Need to synchronize on instance object to avoid connection allocation problem when calling the api
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