package com.peerislands.orders.auth.service;

import com.peerislands.orders.auth.payload.request.LoginRequest;
import com.peerislands.orders.auth.payload.request.SignupRequest;
import com.peerislands.orders.auth.payload.response.JwtResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public interface AuthService {
    void register(@NotNull @Valid SignupRequest signUpRequest);

    JwtResponse authenticate(@NotNull @Valid LoginRequest loginRequest);
}
