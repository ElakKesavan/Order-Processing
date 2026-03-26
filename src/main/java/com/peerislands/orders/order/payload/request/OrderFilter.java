package com.peerislands.orders.order.payload.request;

import com.peerislands.orders.order.model.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Order filtration criteria", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
public class OrderFilter {
    @Schema(
            description = "Filter by order status",
            example = "PENDING",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private OrderStatus status;
}
