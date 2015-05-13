package com.catapult.app.example.beans;

import java.io.Serializable;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.catapult.app.example.adapters.UserAdapter;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class User implements Serializable {

    private static final long serialVersionUID = 4870449978302850567L;

    private String userName;
    private String password;
    private Domain domain;
    private Endpoint endpoint;
    private String phoneNumber;
    private String userUrl;
    private Application application;

    public User(){};

    /**
     * Adapter based constructor.
     * @param userAdapter
     */
    public User(final UserAdapter userAdapter) {
        this.userName = userAdapter.getUserName();
        this.password = userAdapter.getPassword();
    }

    /**
     * @return the userName
     */
    public String getUserName() {
        return userName;
    }
    /**
     * @param userName the userName to set
     */
    public void setUserName(final String userName) {
        this.userName = userName;
    }
    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }
    /**
     * @param password the password to set
     */
    public void setPassword(final String password) {
        this.password = password;
    }
    /**
     * @return the domain
     */
    public Domain getDomain() {
        return domain;
    }
    /**
     * @param domain the domain to set
     */
    public void setDomain(final Domain domain) {
        this.domain = domain;
    }
    /**
     * @return the endpoint
     */
    public Endpoint getEndpoint() {
        return endpoint;
    }
    /**
     * @param endpoint the endpoint to set
     */
    public void setEndpoint(final Endpoint endpoint) {
        this.endpoint = endpoint;
    }

    /**
     * Merge the user properties.
     * @param userAdapter the user adapter with new properties.
     */
    public void mergeProperties(final UserAdapter userAdapter) {
        this.userName = userAdapter.getUserName();
        this.password = userAdapter.getPassword();
    }

    /**
     * @return the phoneNumber
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * @param phoneNumber the phoneNumber to set
     */
    public void setPhoneNumber(final String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * @return the application
     */
    public Application getApplication() {
        return application;
    }

    /**
     * @param application the application to set
     */
    public void setApplication(final Application application) {
        this.application = application;
    }

    /**
     * @return the userUrl
     */
    public String getUserUrl() {
        return userUrl;
    }

    /**
     * @param userUrl the userUrl to set
     */
    public void setUserUrl(final String userUrl) {
        this.userUrl = userUrl;
    }
}