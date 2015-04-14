package com.catapult.app.example.services;

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

                // Case Incoming call from Endpoint
                if (event.getProperty("from") != null) {
                    if (sipPattern.matcher(event.getProperty("from")).find()) {
                        createEndpointCall(event, userName, baseAppUrl);
                        return;
                    }
                }
                // Case Incoming call to Endpoint
                else if (event.getProperty("to") != null) {
                    if (sipPattern.matcher(event.getProperty("to")).find()) {
                        createEndpointCall(event, userName, baseAppUrl);
                        return;
                    }
                }
                LOG.info("Received incoming call FROM or TO NON Mobile Client");

            } else if (event instanceof AnswerEvent) {
                LOG.info("Outgoing call answered " + event);
                bridgeCreatedCalls(event, userName);

            } else {
                LOG.info("Received event for endpoint calls " + event);
                storeCallsEvent(event, userName);
            }
        } catch (Exception e) {
            LOG.error("Could not create event from string", e);
        }
    }

    private void createEndpointCall(final Event event, final String userName, final String baseAppUrl) {
        try {
            User user = userServices.getUser(userName);

            // Create outgoing call using Bandwidth SDK
            Call call = Call.create(event.getProperty("to"), user.getPhoneNumber(),
                    URLUtil.getCallbacksBaseUrl(baseAppUrl, userName), userName);

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
            LOG.error("Could not create outbound call based on call FROM/TO endpoint", e);
        }
    }

    private void bridgeCreatedCalls(final Event event, final String userName) {

        if (userName == null) {
            LOG.error("Could not find the username for call " + event.getProperty("callId"));
            return;
        }

        Map<String, BridgeDetails> bridgeDetailsMap = bridgeMap.get(userName);
        if (bridgeDetailsMap == null) {
            LOG.error("No incoming call mapped to create bridge for user " + userName);
            return;
        }

        BridgeDetails bridgeDetails = bridgeDetailsMap.get(event.getProperty("callId"));
        if (bridgeDetails == null) {
            LOG.error("No incoming call mapped to create bridge for user " + userName
                    + " based on call " + event.getProperty("callId"));
            return;
        }

        // Add the outgoing call event when Answered
        bridgeDetails.getOutgoingCall().addEvent(event);

        try {
            // Bridge the incoming and outgoing calls using Bandwidth SDK
            Bridge bridge = Bridge.create(bridgeDetails.getIncomingCall().getCallId(),
                    bridgeDetails.getOutgoingCall().getCallId());

            bridgeDetails.setBridgeId(bridge.getId());
        } catch (Exception e) {
            LOG.error("Bridge could not be created for " + bridgeDetails);
        }
    }

    private void storeCallsEvent(final Event event, final String userName) {

        if (userName == null) {
            LOG.error("Could not find the username for call " + event.getProperty("callId"));
            return;
        }

        if (event.getProperty("callId") == null) {
            LOG.error("Could not find the callId on event " + event);
            return;
        }

        Map<String, BridgeDetails> bridgeDetailsMap = bridgeMap.get(userName);
        if (bridgeDetailsMap == null) {
            LOG.error("No incoming call mapped to create bridge for user " + userName);
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