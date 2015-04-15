package com.catapult.app.example.configuration;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.bandwidth.sdk.BandwidthClient;

@Component
public class UserConfiguration {

    @Value(value = "${sandbox.user.id:}")
    private String userId;
    
    @Value(value = "${sandbox.api.key:}")
    private String userApiKey;
    
    @Value(value = "${sandbox.api.secret:}")
    private String userApiSecret;
    
    @Value(value = "${sandbox.api.url:}")
    private String apiUrl;
    
    @Value(value = "${sandbox.api.version:}")
    private String apiVersion;
   
<<<<<<< HEAD
    @PostConstruct
    public void userConfiguration() {
        BandwidthClient bandwidthClient = BandwidthClient.getInstance();
        if(!StringUtils.isBlank(userId) && !StringUtils.isBlank(userApiKey) && !StringUtils.isBlank(userApiSecret)) {
            bandwidthClient.setCredentials(userId, userApiKey, userApiSecret);
        }
        if(!StringUtils.isBlank(apiUrl) && !StringUtils.isBlank(apiVersion)) {
            bandwidthClient.setEndpointandVersion(apiUrl, apiVersion);
        }
=======
    @Value(value = "${sandbox.base.url}")
    private String appUrl;
    
    private String currentAppUrl;

    private BandwidthClient bandwidthClient;
    
    @PostConstruct
    public void userConfiguration() {
        this.currentAppUrl = this.appUrl + "/" + this.userId + "/";
        bandwidthClient = BandwidthClient.getInstance();
        bandwidthClient.setCredentials(userId, userApiKey, userApiSecret);
        bandwidthClient.setEndpointandVersion(apiUrl, apiVersion);

>>>>>>> master
    }

    /**
     * @return the userId
     */
    public String getUserId() {
        return userId;
    }
<<<<<<< HEAD
=======

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
        return bandwidthClient;
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
>>>>>>> master
}