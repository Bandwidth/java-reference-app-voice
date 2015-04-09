package com.catapult.app.example.exceptions;

import com.catapult.app.example.constants.ErrorMessages;

public class UserAlreadyExistsException extends Exception {

    private static final long serialVersionUID = -5887590880594086877L;
    
    private final String errorMessage;
    
    public UserAlreadyExistsException(final String userId) {
        this.errorMessage = ErrorMessages.USER_ALREADY_EXISTS + " " + userId;
    }

    /**
     * @return the errorMessage
     */
    public String getErrorMessage() {
        return errorMessage;
    }
}
