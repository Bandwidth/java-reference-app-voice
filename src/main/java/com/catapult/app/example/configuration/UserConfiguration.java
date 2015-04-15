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

    @PostConstruct
    public void userConfiguration() {
        BandwidthClient bandwidthClient = BandwidthClient.getInstance();
        if(!StringUtils.isBlank(userId) && !StringUtils.isBlank(userApiKey) && !StringUtils.isBlank(userApiSecret)) {
            bandwidthClient.setCredentials(userId, userApiKey, userApiSecret);
        }
        if(!StringUtils.isBlank(apiUrl) && !StringUtils.isBlank(apiVersion)) {
            bandwidthClient.setEndpointandVersion(apiUrl, apiVersion);
        }
    }

    /**
     * @return the userId
     */
    public String getUserId() {
        return userId;
    }
}