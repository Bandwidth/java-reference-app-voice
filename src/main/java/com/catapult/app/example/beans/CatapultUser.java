package com.catapult.app.example.beans;

import java.util.ArrayList;
import java.util.List;

public class CatapultUser {

    private com.bandwidth.sdk.model.Domain domain;

    private List<com.bandwidth.sdk.model.PhoneNumber> phoneNumbers = new ArrayList<com.bandwidth.sdk.model.PhoneNumber>();

    /**
     * @return the domain
     */
    public com.bandwidth.sdk.model.Domain getDomain() {
        return domain;
    }

    /**
     * @param domain the domain to set
     */
    public void setDomain(final com.bandwidth.sdk.model.Domain domain) {
        this.domain = domain;
    }

    /**
     * @return the phoneNumbers
     */
    public List<com.bandwidth.sdk.model.PhoneNumber> getPhoneNumbers() {
        return phoneNumbers;
    }

    /**
     * @param phoneNumbers the phoneNumbers to set
     */
    public void setPhoneNumbers(List<com.bandwidth.sdk.model.PhoneNumber> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }
}
