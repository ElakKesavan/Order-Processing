package com.peerislands.orders.payment.service;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MockPaymentService implements PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(MockPaymentService.class);

    public boolean processPayment(@NotNull Long userId, @NotNull BigDecimal amount) {
        logger.info("MockPaymentService: Processing payment of ${} for user ID {}", amount, userId);

        // Simulating a success response based on the MVP plan
        return true;
    }
}
