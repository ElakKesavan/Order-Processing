package com.peerislands.orders.payload.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.Data;

@Data
@Schema(description = "Request body to create a new order")
public class OrderRequest {
    @NotEmpty
    @Valid
    @Schema(description = "List of order items (must not be empty)")
    private List<OrderItemRequest> items;
}
