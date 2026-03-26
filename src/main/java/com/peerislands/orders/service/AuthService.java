package com.peerislands.orders.service;

import com.peerislands.orders.payload.request.LoginRequest;
import com.peerislands.orders.payload.request.SignupRequest;
import com.peerislands.orders.payload.response.JwtResponse;

public interface AuthService {
    void register(SignupRequest signUpRequest);

    JwtResponse authenticate(LoginRequest loginRequest);
}
