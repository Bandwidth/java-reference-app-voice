package com.catapult.app.example.constants;

public interface ErrorMessages {

    String MISSING_ID = "Missing id field.";
    String MISSING_NAME = "Missing name field.";
    String MISSING_PASSWORD = "Missing password field.";
    String PASSWORD_MIN_LENGTH = "Password length must contain at least {0} characters";
    String PASSWORD_MAX_LENGTH = "Password length cannot exceed {0} characters";
    String MISSING_STATE = "Missing state field.";
    
    String USER_NOT_FOUND = "User not found.";
    String USER_ALREADY_EXISTS = "User already exists.";
    String SEARCH_NUMBER = "Error searching a number";
    String ALLOCATE_NUMBER = "Error allocating a number";
    
    String GENERIC_ERROR = "Unexpected error.";
}
