package com.catapult.app.example.services;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bandwidth.sdk.model.Application;
import com.catapult.app.example.configuration.EndpointsConfiguration;
import com.catapult.app.example.configuration.UserConfiguration;
import com.catapult.app.example.constants.ParametersConstants;

@Service
public class ApplicationServices {

    @Autowired
    private UserConfiguration userConfiguration;
    
    @Autowired
    private EndpointsConfiguration endpointsConfiguration;
    
    public Application create(final String userName, final String baseAppUrl) throws Exception {
        //Create a new Application
        final Map<String, Object> applicationParameters = new HashMap<String, Object>();
        final String userApplicationDescription = "Sandbox created Application for user " + userName;
        //Define the application description
        applicationParameters.put(ParametersConstants.NAME, userApplicationDescription);
        //Define the callback URL
        applicationParameters.put(ParametersConstants.INCOMING_CALL_URL, endpointsConfiguration.getCallbacksBaseUrl(baseAppUrl, userName));
        return Application.create(applicationParameters);
    }
    
    /**
     * 
     * @param applicationId the application to find.
     * @return the found application.
     * @throws Exception 
     */
    public Application findApplication(final String applicationId) throws Exception {
        return Application.get(applicationId);
    }
}
