package com.peerislands.orders.order.exception;

public class UnauthorizedOrderAccessException extends OrderProcessingException {
    public UnauthorizedOrderAccessException(String message) {
        super(message);
    }
}
