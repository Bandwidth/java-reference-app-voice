package com.catapult.app.example.adapters;

import java.io.Serializable;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.catapult.app.example.beans.CallEvents;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class CallbackAdapter implements Serializable {

    private static final long serialVersionUID = -8016944879393169608L;

    private CallEvents callEvents;

    public CallbackAdapter(final CallEvents callEvents) {
        this.callEvents = callEvents;
    }

    public CallEvents getCallEvents() {
        return callEvents;
    }

    public void setCallEvents(CallEvents callEvents) {
        this.callEvents = callEvents;
    }

}