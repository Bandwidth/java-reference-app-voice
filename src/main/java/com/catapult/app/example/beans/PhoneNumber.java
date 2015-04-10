package com.catapult.app.example.beans;

import java.io.Serializable;

import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class PhoneNumber implements Serializable {

    private static final long serialVersionUID = 3597858643900977721L;
    
    private String number;
    private String nationalNumber;
    private String name;
    private String city;
    private String state;
    
    public PhoneNumber() { }
    
    /**
     * Custom constructor.
     * @param phoneNumber the SDK PhoneNumber
     */
    public PhoneNumber(final com.bandwidth.sdk.model.PhoneNumber phoneNumber) {
        this.number = phoneNumber.getNumber();
        this.nationalNumber = phoneNumber.getNationalNumber();
        this.name = phoneNumber.getName();
        this.city = phoneNumber.getCity();
        this.state = phoneNumber.getState();
    }

    /**
     * @return the number
     */
    public String getNumber() {
        return number;
    }

    /**
     * @return the nationalNumber
     */
    public String getNationalNumber() {
        return nationalNumber;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the city
     */
    public String getCity() {
        return city;
    }

    /**
     * @return the state
     */
    public String getState() {
        return state;
    }

    /**
     * @param number the number to set
     */
    public void setNumber(final String number) {
        this.number = number;
    }

    /**
     * @param nationalNumber the nationalNumber to set
     */
    public void setNationalNumber(final String nationalNumber) {
        this.nationalNumber = nationalNumber;
    }

    /**
     * @param name the name to set
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * @param city the city to set
     */
    public void setCity(final String city) {
        this.city = city;
    }

    /**
     * @param state the state to set
     */
    public void setState(final String state) {
        this.state = state;
    }
}