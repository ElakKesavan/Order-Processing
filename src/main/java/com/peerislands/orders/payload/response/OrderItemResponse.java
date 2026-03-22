package com.peerislands.orders.payload.response;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class OrderItemResponse {
    private Long id;
    private String productId;
    private Integer quantity;
    private BigDecimal priceAtPurchase;
}
