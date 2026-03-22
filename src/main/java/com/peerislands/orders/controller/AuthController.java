package com.peerislands.orders.controller;

import com.peerislands.orders.payload.request.LoginRequest;
import com.peerislands.orders.payload.request.SignupRequest;
import com.peerislands.orders.payload.response.JwtResponse;
import com.peerislands.orders.payload.response.MessageResponse;
import com.peerislands.orders.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(
            @Valid @RequestBody LoginRequest loginRequest) {
        try {
            JwtResponse response = authService.authenticate(loginRequest);
            return ResponseEntity.ok(response);
        } catch (org.springframework.security.core.AuthenticationException e) {
            return ResponseEntity.status(401)
                    .body(new MessageResponse("Bad credentials: " + e.getMessage()));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<MessageResponse> registerUser(
            @Valid @RequestBody SignupRequest signUpRequest) {
        try {
            authService.register(signUpRequest);
            return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
}
