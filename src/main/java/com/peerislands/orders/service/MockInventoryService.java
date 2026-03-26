package com.peerislands.orders.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
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

    @CircuitBreaker(name = "inventoryService", fallbackMethod = "fallbackCheckAndUpdateInventory")
    public InventoryStatus checkAndUpdateInventory(String productId, int quantity) {
        logger.info("MockInventoryService: Reserving {} units of product {}", quantity, productId);

        int roll = random.nextInt(100);

        // 80% Success, 15% Insufficient Stock, 5% Service Unavailable
        if (roll < 80) {
            return InventoryStatus.SUCCESS;
        } else if (roll < 95) {
            return InventoryStatus.INSUFFICIENT_STOCK;
        } else {
            throw new RuntimeException("Mock Inventory Service Unavailable");
        }
    }

    public InventoryStatus fallbackCheckAndUpdateInventory(String productId, int quantity, Throwable t) {
        logger.error("MockInventoryService fallback triggered for product {} due to: {}", productId, t);
        return InventoryStatus.SERVICE_UNAVAILABLE;
    }
}
