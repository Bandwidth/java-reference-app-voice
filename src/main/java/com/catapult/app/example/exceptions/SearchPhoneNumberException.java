package com.catapult.app.example.exceptions;

import com.catapult.app.example.constants.ErrorMessages;

public class SearchPhoneNumberException extends Exception {

    private static final long serialVersionUID = 6919523305077105568L;
    
    private final String errorMessage;
    
    public SearchPhoneNumberException() {
        this.errorMessage = ErrorMessages.SEARCH_NUMBER;
    }

    /**
     * @return the errorMessage
     */
    public String getErrorMessage() {
        return errorMessage;
    }
}
