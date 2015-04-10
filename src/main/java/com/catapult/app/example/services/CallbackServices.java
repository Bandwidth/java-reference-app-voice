package com.catapult.app.example.services;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.bandwidth.sdk.model.Call;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.catapult.app.example.adapters.CallbackAdapter;
import com.catapult.app.example.beans.BridgeDetails;

@Service
public class CallbackServices {

    private static final Logger LOG = Logger.getLogger(CallbackServices.class);

    private static final Pattern sipPattern = Pattern.compile("^sip:.+@.+");

    private final Map<String, Map<String, BridgeDetails>> bridgeMap = new HashMap<>();

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

//        Call call = Call.create(to, from, callbackUrl, tag);
    }

    private void bridgeCreatedCalls(final CallbackAdapter callbackAdapter) {

    }

    private String findCallbackUser(final CallbackAdapter callbackAdapter) {

        return null;
    }

}