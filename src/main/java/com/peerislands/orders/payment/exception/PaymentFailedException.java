package com.peerislands.orders.payment.exception;

import com.peerislands.orders.order.exception.OrderProcessingException;

public class PaymentFailedException extends OrderProcessingException {
    public PaymentFailedException(String message) {
        super(message);
    }
}
