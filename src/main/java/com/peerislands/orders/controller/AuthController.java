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
import lombok.RequiredArgsConstructor;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "User registration and login endpoints")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

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
                                        schema = @Schema(implementation = ProblemDetail.class)))
            })
    public ResponseEntity<JwtResponse> authenticateUser(
            @Valid @RequestBody LoginRequest loginRequest) {
        JwtResponse response = authService.authenticate(loginRequest);
        return ResponseEntity.ok(response);
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
                                        schema = @Schema(implementation = ProblemDetail.class)))
            })
    public ResponseEntity<MessageResponse> registerUser(
            @Valid @RequestBody SignupRequest signUpRequest) {
        authService.register(signUpRequest);
        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }
}
