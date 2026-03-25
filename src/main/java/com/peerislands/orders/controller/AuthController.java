package com.peerislands.orders.controller;

import com.peerislands.orders.payload.request.LoginRequest;
import com.peerislands.orders.payload.request.SignupRequest;
import com.peerislands.orders.payload.response.JwtResponse;
import com.peerislands.orders.payload.response.MessageResponse;
import com.peerislands.orders.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "User registration and login endpoints")
public class AuthController {

    @Autowired private AuthService authService;

    @PostMapping("/login")
    @SecurityRequirements()
    @Operation(
            summary = "Authenticate user",
            description = "Validates credentials and returns a JWT access token")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Login successful",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = JwtResponse.class))),
                @ApiResponse(
                        responseCode = "401",
                        description = "Invalid credentials",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = MessageResponse.class)))
            })
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            JwtResponse response = authService.authenticate(loginRequest);
            return ResponseEntity.ok(response);
        } catch (org.springframework.security.core.AuthenticationException e) {
            return ResponseEntity.status(401)
                    .body(new MessageResponse("Bad credentials: " + e.getMessage()));
        }
    }

    @PostMapping("/register")
    @SecurityRequirements()
    @Operation(
            summary = "Register a new user",
            description = "Creates a new CUSTOMER account with the provided email and password")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "User registered successfully",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = MessageResponse.class))),
                @ApiResponse(
                        responseCode = "400",
                        description = "Email already taken or validation error",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = MessageResponse.class)))
            })
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
