package com.peerislands.orders.auth.service;

import com.peerislands.orders.auth.model.User;
import com.peerislands.orders.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthenticationHelperImpl implements AuthenticationHelper {

    private final UserRepository userRepository;

    private final ThreadLocal<User> currentUser = new ThreadLocal<>();

    public void setCurrentUser(User user) {
        currentUser.set(user);
    }

    public User getCurrentUser() {
        User user = currentUser.get();
        if (user == null) {
            throw new IllegalStateException(
                    "No authenticated user found in current thread context.");
        }
        return user;
    }

    public void clear() {
        currentUser.remove();
    }

    public void populateUserContext(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
            User user = getAuthenticatedUser(authentication);
            setCurrentUser(user);
        }
    }

    @Cacheable(value = "users", key = "#authentication.principal.id")
    public User getAuthenticatedUser(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userRepository
                .findById(userDetails.getId())
                .orElseThrow(
                        () ->
                                new IllegalStateException(
                                        "Authenticated user not found in database."));
    }
}
