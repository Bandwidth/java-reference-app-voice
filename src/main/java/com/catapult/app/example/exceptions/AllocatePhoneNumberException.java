package com.catapult.app.example.exceptions;

import com.catapult.app.example.constants.ErrorMessages;

public class AllocatePhoneNumberException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = -7936722976077514407L;
    private final String errorMessage;
    
    public AllocatePhoneNumberException() {
        this.errorMessage = ErrorMessages.ALLOCATE_NUMBER;
    }

    /**
     * @return the errorMessage
     */
    public String getErrorMessage() {
        return errorMessage;
    }

}
