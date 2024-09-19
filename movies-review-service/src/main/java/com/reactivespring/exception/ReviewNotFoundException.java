package com.reactivespring.exception;

public class ReviewNotFoundException extends RuntimeException{

    @SuppressWarnings("unused")
    private String message;
    
    @SuppressWarnings("unused")
    private Throwable ex;

    public ReviewNotFoundException( String message, Throwable ex) {
        super(message, ex);
        this.message = message;
        this.ex = ex;
    }

    public ReviewNotFoundException(String message) {
        super(message);
        this.message = message;
    }
}
