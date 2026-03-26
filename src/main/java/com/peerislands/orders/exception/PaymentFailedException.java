package com.peerislands.orders.exception;

public class PaymentFailedException extends OrderProcessingException {
    public PaymentFailedException(String message) {
        super(message);
    }
}
