package com.catapult.app.example.configuration;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.bandwidth.sdk.BandwidthClient;

@Component
public class UserConfiguration {

    @Value(value = "${sandbox.user.id}")
    private String userId;
    
    @Value(value = "${sandbox.api.key}")
    private String userApiKey;
    
    @Value(value = "${sandbox.api.secret}")
    private String userApiSecret;
    
    @Value(value = "${sandbox.api.url}")
    private String apiUrl;
    
    @Value(value = "${sandbox.api.version}")
    private String apiVersion;
   
    @Value(value = "${sandbox.base.url}")
    private String sandboxBaseUrl;

    @PostConstruct
    public void userConfiguration() {
        System.setProperty("com.bandwidth.userId", userId);
        System.setProperty("com.bandwidth.apiToken", userApiKey);
        System.setProperty("com.bandwidth.apiSecret", userApiSecret);
        System.setProperty("com.bandwidth.apiEndpoint", apiUrl);;
        System.setProperty("com.bandwidth.apiVersion", apiVersion);
    }
    
    /**
     * @return the userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     * @return the sandboxBaseUrl
     */
    public String getSandboxBaseUrl() {
        return sandboxBaseUrl;
    }

}