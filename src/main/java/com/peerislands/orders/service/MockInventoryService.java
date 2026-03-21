package com.peerislands.orders.service;

import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MockInventoryService {

    private static final Logger logger = LoggerFactory.getLogger(MockInventoryService.class);
    private final Random random = new Random();

    public enum InventoryStatus {
        SUCCESS,
        INSUFFICIENT_STOCK,
        SERVICE_UNAVAILABLE
    }

    public InventoryStatus checkAndUpdateInventory(String productId, int quantity) {
        logger.info("MockInventoryService: Reserving {} units of product {}", quantity, productId);

        int roll = random.nextInt(100);

        // 80% Success, 10% Insufficient Stock, 10% Service Unavailable
        if (roll < 80) {
            return InventoryStatus.SUCCESS;
        } else if (roll < 90) {
            return InventoryStatus.INSUFFICIENT_STOCK;
        } else {
            return InventoryStatus.SERVICE_UNAVAILABLE;
        }
    }
}
