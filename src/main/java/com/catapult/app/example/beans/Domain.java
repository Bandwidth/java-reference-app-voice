package com.catapult.app.example.beans;

import java.io.Serializable;

import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class Domain implements Serializable {

    private static final long serialVersionUID = 5207350667486711501L;
    
    private String id;
    private String name;
    private String description;
    private String endpointsUrl;
    
    public Domain() { }
    
    public Domain(final com.bandwidth.sdk.model.Domain domain) {
        this.id = domain.getId();
        this.name = domain.getName();
        this.description = domain.getDescription();
        this.endpointsUrl = domain.getEndpointsUrl();
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
     * @return the description
     */
    public String getDescription() {
        return description;
    }
    /**
     * @param description the description to set
     */
    public void setDescription(final String description) {
        this.description = description;
    }
    /**
     * @return the endpointsUrl
     */
    public String getEndpointsUrl() {
        return endpointsUrl;
    }
    /**
     * @param endpointsUrl the endpointsUrl to set
     */
    public void setEndpointsUrl(final String endpointsUrl) {
        this.endpointsUrl = endpointsUrl;
    }
}
