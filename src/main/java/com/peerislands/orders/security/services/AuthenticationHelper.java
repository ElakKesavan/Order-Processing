package com.peerislands.orders.security.services;

import com.peerislands.orders.model.User;
import com.peerislands.orders.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationHelper {

    private final UserRepository userRepository;

    @Autowired
    public AuthenticationHelper(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getAuthenticatedUser(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userRepository
                .findById(userDetails.getId())
                .orElseThrow(
                        () -> new IllegalStateException(
                                "Authenticated user not found in database."));
    }
}
