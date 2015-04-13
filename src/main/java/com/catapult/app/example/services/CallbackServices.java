package com.catapult.app.example.services;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.bandwidth.sdk.model.Call;
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

    private final Map<String, Map<String, BridgeDetails>> bridgeMap = new ConcurrentHashMap<>();

    @Autowired
    private UserServices userServices;

    @Autowired
    private EndpointsConfiguration endpointsConfiguration;

    /**
     * 
     * @param callbackAdapter
     */
    public void handleCallback(final CallbackAdapter callbackAdapter) {
        
        if ("incomingcall".equalsIgnoreCase(callbackAdapter.getEventType())) {

            // Case Incoming call from Endpoint
            if (callbackAdapter.getFrom() != null) {
                if (sipPattern.matcher(callbackAdapter.getFrom()).find()) {
                    createCallFromEndpoint(callbackAdapter);
                    return;
                }
            }
            // Case Incoming call to Endpoint
            else if (callbackAdapter.getTo() != null) {
                if (sipPattern.matcher(callbackAdapter.getTo()).find()) {

                    createCallToEndpoint(callbackAdapter);
                    return;
                }
            }
            LOG.info("Received incoming call from NON Mobile Client");

        } else if ("answer".equalsIgnoreCase(callbackAdapter.getEventType())) {
            bridgeCreatedCalls(callbackAdapter);

        } else {
            // Save the event by call Id
        }
    }

    private void createCallFromEndpoint(final CallbackAdapter callbackAdapter) {

    }

    private void createCallToEndpoint(final CallbackAdapter callbackAdapter) {
        try {
            User user = userServices.getUserByEndpoint(callbackAdapter.getTo());

            Call call = Call.create(user.getEndpoint().getSipUri(), user.getNumber(),
                    endpointsConfiguration.getCallbacksBaseUrl(), user.getUserName());

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

    private void bridgeCreatedCalls(final CallbackAdapter callbackAdapter) {

    }

    private String findCallbackUser(final CallbackAdapter callbackAdapter) {

        return null;
    }

}