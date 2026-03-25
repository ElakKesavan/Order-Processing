package com.peerislands.orders.exception;

public class OrderNotFoundException extends OrderProcessingException {
    public OrderNotFoundException(String message) {
        super(message);
    }
}
