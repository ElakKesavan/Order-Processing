package com.peerislands.orders.order.exception;

public class InvalidOrderStatusTransitionException extends OrderProcessingException {
    public InvalidOrderStatusTransitionException(String message) {
        super(message);
    }
}
