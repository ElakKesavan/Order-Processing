package com.peerislands.orders.order.payload.request;

import com.peerislands.orders.order.model.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Request body to update an order's status")
public class UpdateOrderStatusRequest {
    @NotNull(message = "Status cannot be null")
    @Schema(
            description = "The new status for the order",
            example = "SHIPPED",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private OrderStatus status;
}
