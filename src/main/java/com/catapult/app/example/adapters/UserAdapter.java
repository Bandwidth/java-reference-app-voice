package com.catapult.app.example.adapters;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.catapult.app.example.constants.ErrorMessages;
import com.catapult.app.example.exceptions.MissingFieldsException;

public class UserAdapter implements Serializable {

    private static final long serialVersionUID = 869181531769762934L;

    private static final int MIN_PASSWORD_LENGTH = 5;

    private static final int MAX_PASSWORD_LENGTH = 25;

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

        if (StringUtils.isBlank(this.userName)) {
            errors.put("name", ErrorMessages.MISSING_NAME);
        }

        if (StringUtils.isBlank(this.password)) {
            errors.put("password", ErrorMessages.MISSING_PASSWORD);

        } else if (this.password.length() <= MIN_PASSWORD_LENGTH) {
            errors.put("password", MessageFormat.format(ErrorMessages.PASSWORD_MIN_LENGTH, MIN_PASSWORD_LENGTH));

        } else if (this.password.length() > MAX_PASSWORD_LENGTH) {
            errors.put("password", MessageFormat.format(ErrorMessages.PASSWORD_MAX_LENGTH, MAX_PASSWORD_LENGTH));
        }

        if (!errors.isEmpty()) {
            throw new MissingFieldsException(errors);
        }
    }
}