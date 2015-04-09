package com.catapult.app.example.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(value = "singleton")
public class EndpointsConfiguration {

    @Autowired
    private UserConfiguration userConfiguration;
    
    /**
     * Get the user resources URL.
     * @return The user resources base URL.
     */
    public String getUsersBaseUrl() {
        return null;
    }
    
    /**
     * Configure the operations callback URL.
     * @return the callbacks URL.
     */
    public String getCallbacksBaseUrl() {
        return userConfiguration.getAppUrl() + userConfiguration.getUserId() + "/callback";
    }
    
}
