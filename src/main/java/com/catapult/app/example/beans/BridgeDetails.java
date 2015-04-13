package com.catapult.app.example.beans;

import java.io.Serializable;

public class BridgeDetails implements Serializable {

    private static final long serialVersionUID = 5511227084578363154L;

    private String bridgeId;

    private CallDetails incomingCall;

    private CallDetails outgoingCall;

    public String getBridgeId() {
        return bridgeId;
    }

    public void setBridgeId(String bridgeId) {
        this.bridgeId = bridgeId;
    }

    /**
     * @return the incomingCall
     */
    public CallDetails getIncomingCall() {
        return incomingCall;
    }

    /**
     * @return the outgoingCall
     */
    public CallDetails getOutgoingCall() {
        return outgoingCall;
    }

    /**
     * @param incomingCall the incomingCall to set
     */
    public void setIncomingCall(CallDetails incomingCall) {
        this.incomingCall = incomingCall;
    }

    /**
     * @param outgoingCall the outgoingCall to set
     */
    public void setOutgoingCall(CallDetails outgoingCall) {
        this.outgoingCall = outgoingCall;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("BridgeDetails{");
        sb.append("bridgeId='").append(bridgeId).append('\'');
        sb.append(", incomingCall=").append(incomingCall);
        sb.append(", outgoingCall=").append(outgoingCall);
        sb.append('}');
        return sb.toString();
    }
}
