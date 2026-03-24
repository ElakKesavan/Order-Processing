package com.peerislands.orders.payload.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Generic message response")
public class MessageResponse {
    @Schema(description = "Response message", example = "Operation completed successfully")
    private String message;
}
