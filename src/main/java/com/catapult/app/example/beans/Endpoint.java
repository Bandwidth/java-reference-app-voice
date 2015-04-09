package com.catapult.app.example.beans;

import java.io.Serializable;

import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class Endpoint implements Serializable {

    private static final long serialVersionUID = -2713186295662807523L;

    private String id;
    private String name;
    private String domainId;
    private boolean enabled;
    private String sipUri;
    private Credentials credentials;
    
    public Endpoint() {}
    
    public Endpoint(final com.bandwidth.sdk.model.Endpoint endpoint) {
        this.id = endpoint.getId();
        this.name = endpoint.getName();
        this.domainId = endpoint.getDomainId();
        this.enabled = endpoint.isEnabled();
        this.sipUri = endpoint.getSipUri();
        this.setCredentials(new Credentials(endpoint.getCredentials()));
    }
    
    /**
     * @return the id
     */
    public String getId() {
        return id;
    }
    /**
     * @param id the id to set
     */
    public void setId(final String id) {
        this.id = id;
    }
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }
    /**
     * @param name the name to set
     */
    public void setName(final String name) {
        this.name = name;
    }
    /**
     * @return the domainId
     */
    public String getDomainId() {
        return domainId;
    }
    /**
     * @param domainId the domainId to set
     */
    public void setDomainId(final String domainId) {
        this.domainId = domainId;
    }
    /**
     * @return the enabled
     */
    public boolean isEnabled() {
        return enabled;
    }
    /**
     * @param enabled the enabled to set
     */
    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }
    /**
     * @return the sipUri
     */
    public String getSipUri() {
        return sipUri;
    }
    /**
     * @param sipUri the sipUri to set
     */
    public void setSipUri(final String sipUri) {
        this.sipUri = sipUri;
    }

    /**
     * @return the credentials
     */
    public Credentials getCredentials() {
        return credentials;
    }

    /**
     * @param credentials the credentials to set
     */
    public void setCredentials(final Credentials credentials) {
        this.credentials = credentials;
    }
}
