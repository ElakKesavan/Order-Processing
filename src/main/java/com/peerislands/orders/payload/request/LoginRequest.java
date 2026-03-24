package com.peerislands.orders.payload.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Login credentials")
public class LoginRequest {
    @NotBlank
    @Schema(description = "User email address", example = "customer@example.com")
    private String email;

    @NotBlank
    @Schema(description = "User password", example = "secret123")
    private String password;
}
