package com.peerislands.orders.order.exception;

public class OrderNotFoundException extends OrderProcessingException {
    public OrderNotFoundException(String message) {
        super(message);
    }
}
