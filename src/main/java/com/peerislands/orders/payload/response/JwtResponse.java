package com.peerislands.orders.payload.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "JWT authentication response")
public class JwtResponse {
    @Schema(
            description = "JWT access token",
            example = "eyJhbGci...abc123")
    private String accessToken;

    @Schema(description = "Token type", example = "Bearer")
    private String tokenType = "Bearer";

    @Schema(description = "User ID", example = "1")
    private Long id;

    @Schema(description = "User email", example = "customer@example.com")
    private String email;

    @Schema(description = "User role", example = "ROLE_CUSTOMER")
    private String role;

    public JwtResponse(String accessToken, Long id, String email, String role) {
        this.accessToken = accessToken;
        this.id = id;
        this.email = email;
        this.role = role;
    }
}
