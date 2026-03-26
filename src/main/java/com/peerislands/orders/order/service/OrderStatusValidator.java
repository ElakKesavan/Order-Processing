package com.peerislands.orders.order.service;

import com.peerislands.orders.auth.model.Role;
import com.peerislands.orders.auth.model.User;
import com.peerislands.orders.order.exception.InvalidOrderStatusTransitionException;
import com.peerislands.orders.order.model.Order;
import com.peerislands.orders.order.model.OrderStatus;
import jakarta.validation.constraints.NotNull;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class OrderStatusValidator {

    private static final Map<Role, Map<OrderStatus, Set<OrderStatus>>> VALID_TRANSITIONS =
            new EnumMap<>(Role.class);

    static {
        // Customer transitions
        Map<OrderStatus, Set<OrderStatus>> customerTransitions = new EnumMap<>(OrderStatus.class);
        customerTransitions.put(OrderStatus.PENDING, Set.of(OrderStatus.CANCELLED));
        VALID_TRANSITIONS.put(Role.CUSTOMER, customerTransitions);

        // Admin transitions - Allow everything except PENDING as target and DELIVERED -> SHIPPED
        Map<OrderStatus, Set<OrderStatus>> adminTransitions = new EnumMap<>(OrderStatus.class);
        for (OrderStatus from : OrderStatus.values()) {
            Set<OrderStatus> canMoveTo = EnumSet.allOf(OrderStatus.class);
            canMoveTo.remove(OrderStatus.PENDING); // Cannot manually reset to PENDING
            if (from == OrderStatus.DELIVERED) {
                canMoveTo.remove(OrderStatus.SHIPPED); // Cannot move DELIVERED back to SHIPPED
            }
            adminTransitions.put(from, canMoveTo);
        }
        VALID_TRANSITIONS.put(Role.ADMIN, adminTransitions);
    }

    public void validateTransition(
            @NotNull Order order, @NotNull OrderStatus newStatus, @NotNull User actor) {
        Role role = actor.getRole();
        OrderStatus currentStatus = order.getStatus();

        Map<OrderStatus, Set<OrderStatus>> roleTransitions = VALID_TRANSITIONS.get(role);
        if (roleTransitions == null || !roleTransitions.containsKey(currentStatus)) {
            throwInvalidTransition(role, currentStatus, newStatus);
        }

        Set<OrderStatus> allowedNextStatuses = roleTransitions.get(currentStatus);
        if (allowedNextStatuses == null || !allowedNextStatuses.contains(newStatus)) {
            throwInvalidTransition(role, currentStatus, newStatus);
        }
    }

    private void throwInvalidTransition(
            @NotNull Role role, @NotNull OrderStatus current, @NotNull OrderStatus next) {
        if (role == Role.CUSTOMER) {
            if (next != OrderStatus.CANCELLED) {
                throw new InvalidOrderStatusTransitionException(
                        "Customers can only update status to CANCELLED.");
            }
            if (current != OrderStatus.PENDING) {
                throw new InvalidOrderStatusTransitionException(
                        "Orders can only be cancelled while in PENDING status.");
            }
        }

        if (role == Role.ADMIN) {
            if (next == OrderStatus.PENDING) {
                throw new InvalidOrderStatusTransitionException(
                        "Cannot manually reset an order to PENDING.");
            }
            if (current == OrderStatus.DELIVERED && next == OrderStatus.SHIPPED) {
                throw new InvalidOrderStatusTransitionException(
                        "Cannot move a delivered order back to SHIPPED.");
            }
        }

        throw new InvalidOrderStatusTransitionException(
                String.format(
                        "Invalid status transition from %s to %s for role %s",
                        current, next, role));
    }
}
