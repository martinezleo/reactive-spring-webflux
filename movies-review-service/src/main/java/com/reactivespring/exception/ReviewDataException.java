package com.reactivespring.exception;

public class ReviewDataException extends RuntimeException {
    
    @SuppressWarnings("unused")
    private String message;

    public ReviewDataException(String s) {
        super(s);
        this.message=s;
    }
}
