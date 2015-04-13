package com.catapult.app.example.services;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bandwidth.sdk.model.Bridge;
import com.bandwidth.sdk.model.Call;
import com.catapult.app.example.adapters.CallbackAdapter;
import com.catapult.app.example.beans.BridgeDetails;
import com.catapult.app.example.beans.CallDetails;
import com.catapult.app.example.beans.User;
import com.catapult.app.example.configuration.EndpointsConfiguration;
import com.catapult.app.example.exceptions.UserNotFoundException;

@Service
public class CallbackServices {

    private static final Logger LOG = Logger.getLogger(CallbackServices.class);

    private static final Pattern sipPattern = Pattern.compile("^sip:.+@.+");

    private static final Map<String, Map<String, BridgeDetails>> bridgeMap = new ConcurrentHashMap<>();

    @Autowired
    private UserServices userServices;

    @Autowired
    private EndpointsConfiguration endpointsConfiguration;

    /**
     * 
     * @param callbackAdapter
     */
    public void handleCallback(final CallbackAdapter callbackAdapter, final String userName, final String baseAppUrl) {

        if ("incomingcall".equalsIgnoreCase(callbackAdapter.getEventType())) {

            // Case Incoming call from Endpoint
            if (callbackAdapter.getFrom() != null) {
                if (sipPattern.matcher(callbackAdapter.getFrom()).find()) {
                    createCallFromEndpoint(callbackAdapter, userName, baseAppUrl);
                    return;
                }
            }
            // Case Incoming call to Endpoint
            else if (callbackAdapter.getTo() != null) {
                if (sipPattern.matcher(callbackAdapter.getTo()).find()) {
                    createCallToEndpoint(callbackAdapter, userName, baseAppUrl);
                    return;
                }
            }
            LOG.info("Received incoming call FROM or TO NON Mobile Client");

        } else if ("answer".equalsIgnoreCase(callbackAdapter.getEventType())) {
            bridgeCreatedCalls(callbackAdapter, userName, baseAppUrl);

        } else {
            // TODO: Should we save the event by call Id
        }
    }

    private void createCallFromEndpoint(final CallbackAdapter callbackAdapter, final String userName, final String baseAppUrl) {
        try {
            User user = userServices.getUser(userName);

            Call call = Call.create(callbackAdapter.getTo(), user.getNumber(),
                    endpointsConfiguration.getCallbacksBaseUrl(baseAppUrl, userName), userName);

            Map<String, BridgeDetails> bridgeDetailsMap = bridgeMap.get(user.getUserName());
            if (bridgeDetailsMap == null) {
                bridgeDetailsMap = new ConcurrentHashMap<String, BridgeDetails>();
                bridgeMap.put(user.getUserName(), bridgeDetailsMap);
            }

            BridgeDetails bridgeDetails = new BridgeDetails();

            CallDetails callDetails1 = new CallDetails(callbackAdapter.getCallId());
            callDetails1.addCallback(callbackAdapter);

            CallDetails callDetails2 = new CallDetails(call.getId());

            bridgeDetails.setCall1(callDetails1);
            bridgeDetails.setCall1(callDetails2);

            bridgeDetailsMap.put(call.getId(), bridgeDetails);

        } catch (UserNotFoundException e) {
            LOG.error("User not found to create call", e);

        } catch (Exception e) {
            LOG.error("Could not create outbound call based on call FROM endpoint", e);
        }
    }

    private void createCallToEndpoint(final CallbackAdapter callbackAdapter, final String userName, final String baseAppUrl) {
        try {
            User user = userServices.getUser(userName);

            Call call = Call.create(user.getEndpoint().getSipUri(), user.getNumber(),
                    endpointsConfiguration.getCallbacksBaseUrl(baseAppUrl, userName), userName);

            Map<String, BridgeDetails> bridgeDetailsMap = bridgeMap.get(user.getUserName());
            if (bridgeDetailsMap == null) {
                bridgeDetailsMap = new ConcurrentHashMap<String, BridgeDetails>();
                bridgeMap.put(user.getUserName(), bridgeDetailsMap);
            }

            BridgeDetails bridgeDetails = new BridgeDetails();

            CallDetails callDetails1 = new CallDetails(callbackAdapter.getCallId());
            callDetails1.addCallback(callbackAdapter);

            CallDetails callDetails2 = new CallDetails(call.getId());

            bridgeDetails.setCall1(callDetails1);
            bridgeDetails.setCall1(callDetails2);

            bridgeDetailsMap.put(call.getId(), bridgeDetails);

        } catch (UserNotFoundException e) {
            LOG.error("User not found to create call", e);

        } catch (Exception e) {
            LOG.error("Could not create outbound call based on call TO endpoint", e);
        }
    }

    private void bridgeCreatedCalls(final CallbackAdapter callbackAdapter, final String userName, final String baseAppUrl) {

        if (userName == null) {
            LOG.error("Could not find the username on call tag for call " + callbackAdapter.getCallId());
            return;
        }

        Map<String, BridgeDetails> bridgeDetailsMap = bridgeMap.get(userName);
        if (bridgeDetailsMap == null) {
            LOG.error("No incoming call mapped to create bridge for user " + callbackAdapter.getTag());
        }

        BridgeDetails bridgeDetails = bridgeDetailsMap.get(callbackAdapter.getCallId());
        if (bridgeDetails == null) {
            LOG.error("No incoming call mapped to create bridge for user " + callbackAdapter.getTag()
                    + " based on call " + callbackAdapter.getCallId());
        }

        bridgeDetails.getCall2().addCallback(callbackAdapter);

        try {
            Bridge bridge = Bridge.create(bridgeDetails.getCall1().getCallId(),
                    bridgeDetails.getCall2().getCallId());

            bridgeDetails.setBridgeId(bridge.getId());
        } catch (Exception e) {
            LOG.error("Bridge could not be created for " + bridgeDetails);
        }
    }
}