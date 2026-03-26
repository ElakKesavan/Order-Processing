package com.peerislands.orders.service;

import com.peerislands.orders.model.Role;
import com.peerislands.orders.model.User;
import com.peerislands.orders.payload.request.LoginRequest;
import com.peerislands.orders.payload.request.SignupRequest;
import com.peerislands.orders.payload.response.JwtResponse;
import com.peerislands.orders.repository.UserRepository;
import com.peerislands.orders.security.jwt.JwtUtils;
import com.peerislands.orders.security.services.UserDetailsImpl;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;

    private final UserRepository userRepository;

    private final PasswordEncoder encoder;

    private final JwtUtils jwtUtils;

    @Transactional
    public void register(@NotNull @Valid SignupRequest signUpRequest) {
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new IllegalArgumentException("Error: Email is already in use!");
        }

        User user =
                new User(
                        signUpRequest.getEmail(),
                        encoder.encode(signUpRequest.getPassword()),
                        Role.CUSTOMER // Default Role for direct REST API registrations based on MVP
                        );

        userRepository.save(user);
    }

    @Transactional
    public JwtResponse authenticate(@NotNull @Valid LoginRequest loginRequest) {
        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String role = userDetails.getAuthorities().iterator().next().getAuthority();

        return new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(), role);
    }
}
