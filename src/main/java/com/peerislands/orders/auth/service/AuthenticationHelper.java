package com.peerislands.orders.auth.service;

import com.peerislands.orders.auth.model.User;
import org.springframework.security.core.Authentication;

public interface AuthenticationHelper {
    void setCurrentUser(User user);

    User getCurrentUser();

    void clear();

    void populateUserContext(Authentication authentication);

    User getAuthenticatedUser(Authentication authentication);
}
