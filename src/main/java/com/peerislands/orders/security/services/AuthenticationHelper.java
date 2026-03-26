package com.peerislands.orders.security.services;

import com.peerislands.orders.model.User;
import org.springframework.security.core.Authentication;

public interface AuthenticationHelper {
    void setCurrentUser(User user);

    User getCurrentUser();

    void clear();

    void populateUserContext(Authentication authentication);

    User getAuthenticatedUser(Authentication authentication);
}
