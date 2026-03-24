package com.peerislands.orders.payload.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import lombok.Data;

@Data
@Schema(description = "Individual order item details")
public class OrderItemResponse {
    @Schema(description = "Order item ID", example = "1001")
    private Long id;

    @Schema(description = "Product identifier", example = "PROD-001")
    private String productId;

    @Schema(description = "Quantity ordered", example = "2")
    private Integer quantity;

    @Schema(description = "Price at the time of purchase", example = "29.99")
    private BigDecimal priceAtPurchase;
}
