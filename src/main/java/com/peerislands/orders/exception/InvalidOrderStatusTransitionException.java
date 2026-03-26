package com.peerislands.orders.exception;

public class InvalidOrderStatusTransitionException extends OrderProcessingException {
    public InvalidOrderStatusTransitionException(String message) {
        super(message);
    }
}
