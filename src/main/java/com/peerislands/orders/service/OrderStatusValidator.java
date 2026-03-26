package com.peerislands.orders.service;

import com.peerislands.orders.exception.InvalidOrderStatusTransitionException;
import com.peerislands.orders.model.Order;
import com.peerislands.orders.model.OrderStatus;
import com.peerislands.orders.model.Role;
import com.peerislands.orders.model.User;
import org.springframework.stereotype.Component;

@Component
public class OrderStatusValidator {

    public void validateTransition(Order order, OrderStatus newStatus, User actor) {
        if (actor.getRole() == Role.CUSTOMER) {
            validateCustomerTransition(order, newStatus);
        } else if (actor.getRole() == Role.ADMIN) {
            validateAdminTransition(order, newStatus);
        }
    }

    private void validateCustomerTransition(Order order, OrderStatus newStatus) {
        if (newStatus != OrderStatus.CANCELLED) {
            throw new InvalidOrderStatusTransitionException(
                    "Customers can only update status to CANCELLED.");
        }
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new InvalidOrderStatusTransitionException(
                    "Orders can only be cancelled while in PENDING status.");
        }
    }

    private void validateAdminTransition(Order order, OrderStatus newStatus) {
        if (newStatus == OrderStatus.PENDING) {
            throw new InvalidOrderStatusTransitionException(
                    "Cannot manually reset an order to PENDING.");
        }
        if (order.getStatus() == OrderStatus.DELIVERED && newStatus == OrderStatus.SHIPPED) {
            throw new InvalidOrderStatusTransitionException(
                    "Cannot move a delivered order back to SHIPPED.");
        }
    }
}
