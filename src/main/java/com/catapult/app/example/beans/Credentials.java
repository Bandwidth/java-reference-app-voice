package com.catapult.app.example.beans;

import java.io.Serializable;

public class Credentials implements Serializable {

    private static final long serialVersionUID = 8442887578655772726L;
    
    private String realm;
    private String username;
    
    public Credentials() { }
    
    public Credentials(final com.bandwidth.sdk.model.Credentials credential) {
        this.realm = credential.getRealm();
        this.username = credential.getUserName();
    }
    
    /**
     * @return the realm
     */
    public String getRealm() {
        return realm;
    }
    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }
    /**
     * @param realm the realm to set
     */
    public void setRealm(final String realm) {
        this.realm = realm;
    }
    /**
     * @param username the username to set
     */
    public void setUsername(final String username) {
        this.username = username;
    }
}