package com.reactivespring.exception;

public class ReviewsClientException extends RuntimeException{
    
    @SuppressWarnings("unused")
    private String message;

    public ReviewsClientException(String message) {
        super(message);
        this.message = message;
    }
}
