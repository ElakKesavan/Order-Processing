package com.peerislands.orders.exception;

public class InventoryUnavailableException extends OrderProcessingException {
    public InventoryUnavailableException(String message) {
        super(message);
    }
}
