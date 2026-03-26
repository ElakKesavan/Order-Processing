package com.peerislands.orders.service;

public interface InventoryService {
    enum InventoryStatus {
        SUCCESS,
        INSUFFICIENT_STOCK,
        SERVICE_UNAVAILABLE
    }

    InventoryStatus checkAndUpdateInventory(String productId, int quantity);
}
