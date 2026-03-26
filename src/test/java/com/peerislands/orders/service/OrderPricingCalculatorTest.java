package com.peerislands.orders.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.peerislands.orders.payload.request.OrderItemRequest;
import com.peerislands.orders.payload.request.OrderRequest;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

public class OrderPricingCalculatorTest {

    private final OrderPricingCalculator calculator = new OrderPricingCalculator();

    @Test
    void calculateTotalAmount_Success() {
        OrderItemRequest item1 = new OrderItemRequest();
        item1.setPrice(new BigDecimal("10.00"));
        item1.setQuantity(2);

        OrderItemRequest item2 = new OrderItemRequest();
        item2.setPrice(new BigDecimal("5.50"));
        item2.setQuantity(3);

        OrderRequest request = new OrderRequest();
        request.setItems(List.of(item1, item2));

        BigDecimal total = calculator.calculateTotalAmount(request);

        assertEquals(new BigDecimal("36.50"), total);
    }
}
