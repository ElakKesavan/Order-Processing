package com.peerislands.orders.payment.service;

import java.math.BigDecimal;

public interface PaymentService {
    boolean processPayment(Long userId, BigDecimal amount);
}
