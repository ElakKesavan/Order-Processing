package com.peerislands.orders.payload.response;

import com.peerislands.orders.model.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
@Schema(description = "Order details response")
public class OrderResponse {
    @Schema(description = "Order ID", example = "101")
    private Long id;

    @Schema(description = "User ID associated with the order", example = "1")
    private Long userId;

    @Schema(description = "User email associated with the order", example = "user@example.com")
    private String userEmail;

    @Schema(description = "Current order status", example = "PENDING")
    private OrderStatus status;

    @Schema(description = "Total order amount", example = "208.98")
    private BigDecimal totalAmount;

    @Schema(description = "List of items in the order")
    private List<OrderItemResponse> items;

    @Schema(description = "Timestamp when the order was created", example = "2026-03-24T04:30:00")
    private LocalDateTime createdAt;

    @Schema(
            description = "Timestamp when the order was last updated",
            example = "2026-03-24T04:30:00")
    private LocalDateTime updatedAt;
}
