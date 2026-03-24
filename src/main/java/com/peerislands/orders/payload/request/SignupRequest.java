package com.peerislands.orders.payload.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "User registration request")
public class SignupRequest {
    @NotBlank
    @Size(max = 255)
    @Email
    @Schema(description = "Valid email address", example = "alice@example.com")
    private String email;

    @NotBlank
    @Size(min = 6, max = 40)
    @Schema(description = "Password (6-40 characters)", example = "alice123")
    private String password;
}
