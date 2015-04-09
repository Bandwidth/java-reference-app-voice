package com.catapult.app.example.configuration;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.bandwidth.sdk.BandwidthClient;

@Component
@Scope(value = "singleton")
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
    private String appUrl;
    
    private String currentAppUrl;
    
    @PostConstruct
    public void userConfiguration() {
        this.currentAppUrl = this.appUrl + "/" + this.userId + "/";
    }
    
    /**
     * @return the userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     * @return the userApiToken
     */
    public String getUserApiKey() {
        return userApiKey;
    }

    /**
     * @return the userApiSecret
     */
    public String getUserApiSecret() {
        return userApiSecret;
    }

    /**
     * @return the apiUrl
     */
    public String getApiUrl() {
        return apiUrl;
    }

    /**
     * @return the apiVersion
     */
    public String getApiVersion() {
        return apiVersion;
    }

    /**
     * @return the userClientuserClient
     */
    public BandwidthClient getUserClient() {
        return BandwidthClient.getInstance();
    }

    /**
     * @return the appUrl
     */
    public String getAppUrl() {
        return appUrl;
    }

    /**
     * @return the currentAppUrl
     */
    public String getCurrentAppUrl() {
        return currentAppUrl;
    }
}