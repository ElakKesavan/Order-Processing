package com.peerislands.orders.payload.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "JWT authentication response")
public class JwtResponse {
    @Schema(description = "JWT access token", example = "eyJhbGci...abc123")
    private String accessToken;

    @Builder.Default
    @Schema(description = "Token type", example = "Bearer")
    private String tokenType = "Bearer";

    @Schema(description = "User ID", example = "1")
    private Long id;

    @Schema(description = "User email", example = "customer@example.com")
    private String email;

    @Schema(description = "User role", example = "ROLE_CUSTOMER")
    private String role;
}
