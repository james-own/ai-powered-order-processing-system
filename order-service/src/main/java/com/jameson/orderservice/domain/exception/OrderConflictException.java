package com.jameson.orderservice.domain.exception;

public class OrderConflictException extends RuntimeException{

    public OrderConflictException(String message) {
        super(message);
    }

    public OrderConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}
