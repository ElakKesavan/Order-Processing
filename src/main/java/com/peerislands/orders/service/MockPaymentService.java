package com.peerislands.orders.service;

import java.math.BigDecimal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MockPaymentService {

    private static final Logger logger = LoggerFactory.getLogger(MockPaymentService.class);

    public boolean processPayment(Long userId, BigDecimal amount) {
        logger.info("MockPaymentService: Processing payment of ${} for user ID {}", amount, userId);

        // Simulating a success response based on the MVP plan
        return true;
    }
}
