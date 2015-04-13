package com.catapult.app.example.services;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.bandwidth.sdk.model.Call;
import com.bandwidth.sdk.model.Bridge;
import com.catapult.app.example.beans.CallDetails;
import com.catapult.app.example.beans.User;
import com.catapult.app.example.configuration.EndpointsConfiguration;
import com.catapult.app.example.configuration.UserConfiguration;
import com.catapult.app.example.exceptions.UserNotFoundException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.catapult.app.example.adapters.CallbackAdapter;
import com.catapult.app.example.beans.BridgeDetails;

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
    public void handleCallback(final CallbackAdapter callbackAdapter, final String userName) {

        if ("incomingcall".equalsIgnoreCase(callbackAdapter.getEventType())) {

            // Case Incoming call from Endpoint
            if (callbackAdapter.getFrom() != null) {
                if (sipPattern.matcher(callbackAdapter.getFrom()).find()) {
                    createCallFromEndpoint(callbackAdapter, userName);
                    return;
                }
            }

            // Case Incoming call to Endpoint
            else if (callbackAdapter.getTo() != null) {
                if (sipPattern.matcher(callbackAdapter.getTo()).find()) {

                    createCallToEndpoint(callbackAdapter, userName);
                    return;
                }
            }
            LOG.info("Received incoming call from NON Mobile Client");

        } else if ("answer".equalsIgnoreCase(callbackAdapter.getEventType())) {
            bridgeCreatedCalls(callbackAdapter, userName);

        } else {
            // TODO: Should we save the event by call Id
        }
    }

    private void createCallFromEndpoint(final CallbackAdapter callbackAdapter, final String userName) {
        //TODO: lvivo
    }

    private void createCallToEndpoint(final CallbackAdapter callbackAdapter, final String userName) {
        try {
            User user = userServices.getUser(userName);

            Call call = Call.create(user.getEndpoint().getSipUri(), user.getNumber(),
                    endpointsConfiguration.getCallbacksBaseUrl(), userName);

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
            LOG.error("Could not create outbound call based on call To endpoint", e);
        }
    }

    private void bridgeCreatedCalls(final CallbackAdapter callbackAdapter, final String userName) {

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