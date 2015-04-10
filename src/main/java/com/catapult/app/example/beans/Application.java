package com.catapult.app.example.beans;

import java.io.Serializable;

public class Application implements Serializable {

    private static final long serialVersionUID = -2683481601276259116L;
    
    private String name;
    private String incomingCallUrl;
    private String incomingSmsUrl;
    private String callbackHttpMethod;
    private String incomingCallFallbackUrl;
    private Long incomingCallUrlCallbackTimeout;
    private Long incomingSmsUrlCallbackTimeout;
    private Boolean autoAnswer;

    public Application() { }
    
    public Application(final com.bandwidth.sdk.model.Application application) {
        this.name = application.getName();
        this.incomingCallUrl = application.getIncomingCallUrl();
        this.incomingSmsUrl = application.getIncomingSmsUrl();
        this.callbackHttpMethod = application.getCallbackHttpMethod();
        this.incomingCallFallbackUrl = application.getIncomingCallFallbackUrl();
        this.incomingCallUrlCallbackTimeout = application.getIncomingCallUrlCallbackTimeout();
        this.incomingSmsUrlCallbackTimeout = application.getIncomingSmsUrlCallbackTimeout();
        this.autoAnswer = application.isAutoAnswer();
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the incomingCallUrl
     */
    public String getIncomingCallUrl() {
        return incomingCallUrl;
    }

    /**
     * @return the incomingSmsUrl
     */
    public String getIncomingSmsUrl() {
        return incomingSmsUrl;
    }

    /**
     * @return the callbackHttpMethod
     */
    public String getCallbackHttpMethod() {
        return callbackHttpMethod;
    }

    /**
     * @return the incomingCallFallbackUrl
     */
    public String getIncomingCallFallbackUrl() {
        return incomingCallFallbackUrl;
    }

    /**
     * @return the incomingCallUrlCallbackTimeout
     */
    public Long getIncomingCallUrlCallbackTimeout() {
        return incomingCallUrlCallbackTimeout;
    }

    /**
     * @return the incomingSmsUrlCallbackTimeout
     */
    public Long getIncomingSmsUrlCallbackTimeout() {
        return incomingSmsUrlCallbackTimeout;
    }

    /**
     * @return the autoAnswer
     */
    public Boolean getAutoAnswer() {
        return autoAnswer;
    }

    /**
     * @param name the name to set
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * @param incomingCallUrl the incomingCallUrl to set
     */
    public void setIncomingCallUrl(final String incomingCallUrl) {
        this.incomingCallUrl = incomingCallUrl;
    }

    /**
     * @param incomingSmsUrl the incomingSmsUrl to set
     */
    public void setIncomingSmsUrl(final String incomingSmsUrl) {
        this.incomingSmsUrl = incomingSmsUrl;
    }

    /**
     * @param callbackHttpMethod the callbackHttpMethod to set
     */
    public void setCallbackHttpMethod(final String callbackHttpMethod) {
        this.callbackHttpMethod = callbackHttpMethod;
    }

    /**
     * @param incomingCallFallbackUrl the incomingCallFallbackUrl to set
     */
    public void setIncomingCallFallbackUrl(final String incomingCallFallbackUrl) {
        this.incomingCallFallbackUrl = incomingCallFallbackUrl;
    }

    /**
     * @param incomingCallUrlCallbackTimeout the incomingCallUrlCallbackTimeout to set
     */
    public void setIncomingCallUrlCallbackTimeout(
            final Long incomingCallUrlCallbackTimeout) {
        this.incomingCallUrlCallbackTimeout = incomingCallUrlCallbackTimeout;
    }

    /**
     * @param incomingSmsUrlCallbackTimeout the incomingSmsUrlCallbackTimeout to set
     */
    public void setIncomingSmsUrlCallbackTimeout(final Long incomingSmsUrlCallbackTimeout) {
        this.incomingSmsUrlCallbackTimeout = incomingSmsUrlCallbackTimeout;
    }

    /**
     * @param autoAnswer the autoAnswer to set
     */
    public void setAutoAnswer(final Boolean autoAnswer) {
        this.autoAnswer = autoAnswer;
    }
}