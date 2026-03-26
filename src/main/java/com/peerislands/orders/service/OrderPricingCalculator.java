package com.peerislands.orders.service;

import com.peerislands.orders.payload.request.OrderItemRequest;
import com.peerislands.orders.payload.request.OrderRequest;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import org.springframework.stereotype.Component;

@Component
public class OrderPricingCalculator {

    public BigDecimal calculateTotalAmount(@NotNull OrderRequest request) {
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (OrderItemRequest itemReq : request.getItems()) {
            BigDecimal itemTotal =
                    itemReq.getPrice().multiply(BigDecimal.valueOf(itemReq.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);
        }
        return totalAmount;
    }
}
