package com.catapult.app.example.generics;


public class GenericResponse<T> {
    
    private T data;

    public GenericResponse() { }
    
    public GenericResponse(T data) {
        super();
        this.data = data;
    }

    /**
     * @return the data
     */
    public T getData() {
        return data;
    }

    /**
     * @param data the data to set
     */
    public void setData(T data) {
        this.data = data;
    }
}
