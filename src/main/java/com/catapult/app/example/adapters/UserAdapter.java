package com.catapult.app.example.adapters;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.catapult.app.example.constants.ErrorMessages;
import com.catapult.app.example.exceptions.MissingFieldsException;

public class UserAdapter implements Serializable {

    private static final long serialVersionUID = 869181531769762934L;
    
    private String userName;
    private String password;

    /**
     * @return the userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @param userName the name to set
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

    @Override
    public String toString() {
        return "UserAdapter{" +
                "userName='" + userName + '\'' +
                '}';
    }
    
    /**
     * Validate the required fields.
     * @throws MissingFieldsException
     */
    public void validate() throws MissingFieldsException {
        
        final Map<String, String> errors = new HashMap<String, String>();
        if(this.userName == null) {
            errors.put("name", ErrorMessages.MISSING_NAME);
        }
        
        if(this.password == null) {
            errors.put("password", ErrorMessages.MISSING_PASSWORD);
        }
        
        if(!errors.isEmpty()) {
            throw new MissingFieldsException(errors);
        }
    }
}