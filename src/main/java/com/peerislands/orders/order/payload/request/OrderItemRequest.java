package com.peerislands.orders.order.payload.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Data;

@Data
@Schema(description = "Individual item within an order")
public class OrderItemRequest {

    @NotBlank
    @Schema(description = "Product identifier", example = "PROD-001")
    private String productId;

    @NotNull
    @Min(1)
    @Schema(description = "Quantity (must be at least 1)", example = "2")
    private Integer quantity;

    @NotNull
    @DecimalMin("0.01")
    @Schema(description = "Unit price (must be at least 0.01)", example = "29.99")
    private BigDecimal price;
}
