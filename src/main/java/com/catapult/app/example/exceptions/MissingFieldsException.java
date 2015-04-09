package com.catapult.app.example.exceptions;

import java.util.Map;

public class MissingFieldsException extends Exception {

    private static final long serialVersionUID = 4527343360594760315L;
    
    private Map<String, String> fields;
    
    public MissingFieldsException() { }
    
    public MissingFieldsException(final Map<String, String> fields) {
        this.fields = fields;
    }

    /**
     * @return the fields
     */
    public Map<String, String> getFields() {
        return fields;
    }
}
