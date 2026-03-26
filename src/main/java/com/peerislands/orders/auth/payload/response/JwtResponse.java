package com.peerislands.orders.auth.payload.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JwtResponse {
    private String accessToken;

    @Builder.Default private String type = "Bearer";

    private Long id;
    private String email;
    private String role;
}
