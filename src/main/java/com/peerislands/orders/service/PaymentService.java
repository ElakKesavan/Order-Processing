package com.peerislands.orders.service;

import java.math.BigDecimal;

public interface PaymentService {
    boolean processPayment(Long userId, BigDecimal amount);
}
