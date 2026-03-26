package com.peerislands.orders.exception;

public class UnauthorizedOrderAccessException extends OrderProcessingException {
    public UnauthorizedOrderAccessException(String message) {
        super(message);
    }
}
