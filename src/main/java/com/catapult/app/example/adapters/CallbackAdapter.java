package com.catapult.app.example.adapters;

import java.io.Serializable;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.catapult.app.example.beans.BridgeDetails;
import com.catapult.app.example.beans.CallDetails;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class CallbackAdapter implements Serializable {

    private static final long serialVersionUID = -8016944879393169608L;

    private String bridgeId;
    private CallDetails incomingCall;
    private CallDetails outgoingCall;


    public CallbackAdapter(final BridgeDetails details) {
        super();
        this.bridgeId = details.getBridgeId();
        this.incomingCall = details.getIncomingCall();
        this.outgoingCall = details.getOutgoingCall();
    }

    /**
     * @return the bridgeId
     */
    public String getBridgeId() {
        return bridgeId;
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
     * @param bridgeId the bridgeId to set
     */
    public void setBridgeId(final String bridgeId) {
        this.bridgeId = bridgeId;
    }

    /**
     * @param incomingCall the incomingCall to set
     */
    public void setIncomingCall(final CallDetails incomingCall) {
        this.incomingCall = incomingCall;
    }

    /**
     * @param outgoingCall the outgoingCall to set
     */
    public void setOutgoingCall(final CallDetails outgoingCall) {
        this.outgoingCall = outgoingCall;
    }
}