package com.catapult.app.example.beans;

import java.io.Serializable;

public class BridgeDetails implements Serializable {

    private static final long serialVersionUID = 5511227084578363154L;
    
    private CallDetails call1;
    private CallDetails call2;
    /**
     * @return the call1
     */
    public CallDetails getCall1() {
        return call1;
    }
    /**
     * @return the call2
     */
    public CallDetails getCall2() {
        return call2;
    }
    /**
     * @param call1 the call1 to set
     */
    public void setCall1(CallDetails call1) {
        this.call1 = call1;
    }
    /**
     * @param call2 the call2 to set
     */
    public void setCall2(CallDetails call2) {
        this.call2 = call2;
    }
}
