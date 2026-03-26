package com.peerislands.orders.order.service;

import com.peerislands.orders.auth.model.Role;
import com.peerislands.orders.auth.model.User;
import com.peerislands.orders.order.exception.UnauthorizedOrderAccessException;
import com.peerislands.orders.order.model.Order;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

@Service
public class OrderAuthorizationService {

    public void validateOrderAccess(@NotNull Order order, @NotNull User user) {
        if (user.getRole() == Role.CUSTOMER && !order.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedOrderAccessException("Unauthorized access to order.");
        }
    }

    public void validateOrderUpdateAccess(@NotNull Order order, @NotNull User user) {
        if (user.getRole() == Role.CUSTOMER && !order.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedOrderAccessException("You can only update your own orders.");
        }
    }
}
