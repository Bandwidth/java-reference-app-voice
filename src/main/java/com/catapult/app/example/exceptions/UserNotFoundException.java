package com.catapult.app.example.exceptions;

import com.catapult.app.example.constants.ErrorMessages;

public class UserNotFoundException extends Exception {

    private static final long serialVersionUID = -4662917571263376640L;
    
    private final String errorMessage;
    
    public UserNotFoundException(final String userId) {
        this.errorMessage = ErrorMessages.USER_NOT_FOUND + " " + userId;
    }

    /**
     * @return the userId
     */
    public String getErrorMessage() {
        return errorMessage;
    }
}
