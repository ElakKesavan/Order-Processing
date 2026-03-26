package com.peerislands.orders.inventory.exception;

import com.peerislands.orders.order.exception.OrderProcessingException;

public class InventoryUnavailableException extends OrderProcessingException {
    public InventoryUnavailableException(String message) {
        super(message);
    }
}
