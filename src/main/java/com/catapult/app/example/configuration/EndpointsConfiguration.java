package com.catapult.app.example.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EndpointsConfiguration {

    @Autowired
    private UserConfiguration userConfiguration;

    /**
     * Configure the operations callback URL.
     * @return the callbacks URL.
     */
    public String getCallbacksBaseUrl() {
        return userConfiguration.getSandboxBaseUrl() + userConfiguration.getUserId() + "/callback";
    }

}
