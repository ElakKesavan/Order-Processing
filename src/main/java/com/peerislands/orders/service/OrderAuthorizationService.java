package com.peerislands.orders.service;

import com.peerislands.orders.exception.UnauthorizedOrderAccessException;
import com.peerislands.orders.model.Order;
import com.peerislands.orders.model.Role;
import com.peerislands.orders.model.User;
import org.springframework.stereotype.Service;

@Service
public class OrderAuthorizationService {

    public void validateOrderAccess(Order order, User user) {
        if (user.getRole() == Role.CUSTOMER && !order.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedOrderAccessException("Unauthorized access to order.");
        }
    }

    public void validateOrderUpdateAccess(Order order, User user) {
        if (user.getRole() == Role.CUSTOMER && !order.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedOrderAccessException("You can only update your own orders.");
        }
    }
}
