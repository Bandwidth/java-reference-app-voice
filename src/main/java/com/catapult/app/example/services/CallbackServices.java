package com.catapult.app.example.services;

import java.text.MessageFormat;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import com.bandwidth.sdk.model.events.AnswerEvent;
import com.bandwidth.sdk.model.events.IncomingCallEvent;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bandwidth.sdk.model.Bridge;
import com.bandwidth.sdk.model.Call;
import com.bandwidth.sdk.model.events.Event;
import com.bandwidth.sdk.model.events.EventBase;
import com.catapult.app.example.beans.BridgeDetails;
import com.catapult.app.example.beans.CallDetails;
import com.catapult.app.example.beans.User;
import com.catapult.app.example.exceptions.UserNotFoundException;
import com.catapult.app.example.util.URLUtil;

@Service
public class CallbackServices {

    private static final Logger LOG = Logger.getLogger(CallbackServices.class);

    private static final Pattern sipPattern = Pattern.compile("^sip:.+@.+");

    private static final Map<String, Map<String, BridgeDetails>> bridgeMap = new ConcurrentHashMap<>();

    @Autowired
    private UserServices userServices;

    /**
     * 
     * @param eventString string containing event data
     */
    public void handleCallback(final String eventString, final String userName, final String baseAppUrl) {
        try {
            Event event = EventBase.createEventFromString(eventString);

            if (event instanceof IncomingCallEvent) {

                // Case Incoming call from Endpoint to a PSTN number
                if (event.getProperty("from") != null) {
                    if (sipPattern.matcher(event.getProperty("from")).find()) {
                        createCallFromEndpoint(event, userName, baseAppUrl);
                        return;
                    }
                }
                // Case Incoming call from PSTN to a number associated to an Endpoint
                else if (event.getProperty("to") != null) {
                        createCallToEndpoint(event, userName, baseAppUrl);
                        return;
                }
            } else if (event instanceof AnswerEvent) {
                LOG.info(MessageFormat.format("Call answered [{0}]", eventString));
                bridgeCalls(event, userName);

            } else {
                LOG.info(MessageFormat.format("Received event for endpoint calls [{0}]", eventString));
                storeCallsEvent(event, userName);
            }
        } catch (Exception e) {
            LOG.error("Could not create event from string", e);
        }
    }

    private void createCallFromEndpoint(final Event event, final String userName, final String baseAppUrl) {
        try {
            User user = userServices.getUser(userName);

            // Create outgoing call using Bandwidth SDK
            Call call = Call.create(event.getProperty("to"), user.getPhoneNumber(),
                    URLUtil.getCallbacksBaseUrl(baseAppUrl, userName), null);

            if (call == null || call.getId() == null) {
                LOG.error(MessageFormat.format("Could not create outgoing call for incoming call [{0}]",
                        event.getProperty("callId")));
                return;
            }

            LOG.info(MessageFormat.format("Created outgoing call [{0}] for incomingCall [{1}]",
                    call.getId(), event.getProperty("callId")));

            Map<String, BridgeDetails> bridgeDetailsMap = bridgeMap.get(userName);
            if (bridgeDetailsMap == null) {
                bridgeDetailsMap = new ConcurrentHashMap<String, BridgeDetails>();
                bridgeMap.put(userName, bridgeDetailsMap);
            }

            BridgeDetails bridgeDetails = new BridgeDetails();

            CallDetails incomingCall = new CallDetails(event.getProperty("callId"));
            incomingCall.addEvent(event);

            // The events for outgoing call will be saved after AnswerEvent
            CallDetails outgoingCall = new CallDetails(call.getId());

            bridgeDetails.setIncomingCall(incomingCall);
            bridgeDetails.setOutgoingCall(outgoingCall);

            // Save the mapped bridge details based on outgoing call created
            bridgeDetailsMap.put(call.getId(), bridgeDetails);

        } catch (UserNotFoundException e) {
            LOG.error("User not found to create call", e);

        } catch (Exception e) {
            LOG.error("Could not create outbound call based on call FROM Endpoint", e);
        }
    }

    private void createCallToEndpoint(final Event event, final String userName, final String baseAppUrl) {
        try {
            User user = userServices.getUser(userName);

            // Create outgoing call using Bandwidth SDK
            Call call = Call.create(user.getEndpoint().getSipUri(), event.getProperty("to"),
                    URLUtil.getCallbacksBaseUrl(baseAppUrl, userName), null);

            if (call == null || call.getId() == null) {
                LOG.error(MessageFormat.format("Could not create outgoing call for incoming call [{0}]",
                        event.getProperty("callId")));
                return;
            }

            LOG.info(MessageFormat.format("Created outgoing call [{0}] for incomingCall [{1}]",
                    call.getId(), event.getProperty("callId")));

            Map<String, BridgeDetails> bridgeDetailsMap = bridgeMap.get(userName);
            if (bridgeDetailsMap == null) {
                bridgeDetailsMap = new ConcurrentHashMap<String, BridgeDetails>();
                bridgeMap.put(userName, bridgeDetailsMap);
            }

            BridgeDetails bridgeDetails = new BridgeDetails();

            CallDetails incomingCall = new CallDetails(event.getProperty("callId"));
            incomingCall.addEvent(event);

            // The events for outgoing call will be saved after AnswerEvent
            CallDetails outgoingCall = new CallDetails(call.getId());

            bridgeDetails.setIncomingCall(incomingCall);
            bridgeDetails.setOutgoingCall(outgoingCall);

            // Save the mapped bridge details based on outgoing call created
            bridgeDetailsMap.put(call.getId(), bridgeDetails);

        } catch (UserNotFoundException e) {
            LOG.error("User not found to create call", e);

        } catch (Exception e) {
            LOG.error("Could not create outbound call based on call TO Endpoint", e);
        }
    }

    private void bridgeCalls(final Event event, final String userName) {

        if (userName == null) {
            LOG.error(MessageFormat.format("Could not find the username for call [{0}]",
                    event.getProperty("callId")));
            return;
        }

        Map<String, BridgeDetails> bridgeDetailsMap = bridgeMap.get(userName);
        if (bridgeDetailsMap == null) {
            LOG.error(MessageFormat.format("No incoming call mapped to create bridge for user [{0}]", userName));
            return;
        }

        BridgeDetails bridgeDetails = bridgeDetailsMap.get(event.getProperty("callId"));
        if (bridgeDetails == null) {
            LOG.error(MessageFormat.format("No outgoing call mapped to create bridge for user [{0}] based on call [{1}]",
                    userName, event.getProperty("callId")));
            return;
        }

        // Add the outgoing call event when Answered
        bridgeDetails.getOutgoingCall().addEvent(event);

        try {
            // Bridge the incoming and outgoing calls using Bandwidth SDK
            String incomingCallId = bridgeDetails.getIncomingCall().getCallId();
            String outgoingCallId = bridgeDetails.getOutgoingCall().getCallId();
            Bridge bridge = Bridge.create(incomingCallId, outgoingCallId);

            if (bridge == null || bridge.getId() == null) {
                LOG.error(MessageFormat.format("Could not bridge calls [{0}, {1}]", incomingCallId, outgoingCallId));
            }

            bridgeDetails.setBridgeId(bridge.getId());

            LOG.info(MessageFormat.format("Calls [{0}, {1}] successfully bridged by [{2}]",
                    incomingCallId, outgoingCallId, bridge.getId()));

        } catch (Exception e) {
            LOG.error("Bridge could not be created for " + bridgeDetails);
        }
    }

    private void storeCallsEvent(final Event event, final String userName) {

        if (userName == null) {
            LOG.error(MessageFormat.format("Could not find the username for call [{0}]",
                    event.getProperty("callId")));
            return;
        }

        if (event.getProperty("callId") == null) {
            LOG.error(MessageFormat.format("Could not find the callId on event [{0}]", event));
            return;
        }

        Map<String, BridgeDetails> bridgeDetailsMap = bridgeMap.get(userName);
        if (bridgeDetailsMap == null) {
            LOG.error(MessageFormat.format("No incoming call mapped to create bridge for user [{0}]", userName));
            return;
        }

        for (BridgeDetails bridgeDetails : bridgeDetailsMap.values()) {

            if (bridgeDetails.getIncomingCall() != null
                    && event.getProperty("callId").equals(bridgeDetails.getIncomingCall().getCallId())) {
                bridgeDetails.getIncomingCall().addEvent(event);
                break;

            } else if (bridgeDetails.getOutgoingCall() != null
                    && event.getProperty("callId").equals(bridgeDetails.getOutgoingCall().getCallId())) {
                bridgeDetails.getOutgoingCall().addEvent(event);
                break;
            }
        }
    }
}