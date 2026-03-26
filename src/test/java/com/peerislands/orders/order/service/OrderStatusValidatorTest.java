package com.peerislands.orders.order.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.peerislands.orders.auth.model.Role;
import com.peerislands.orders.auth.model.User;
import com.peerislands.orders.order.exception.InvalidOrderStatusTransitionException;
import com.peerislands.orders.order.model.Order;
import com.peerislands.orders.order.model.OrderStatus;
import org.junit.jupiter.api.Test;

public class OrderStatusValidatorTest {

    private final OrderStatusValidator validator = new OrderStatusValidator();

    @Test
    void validateTransition_Customer_Cancel_Success() {
        User user = new User("test@test.com", "hash", Role.CUSTOMER);
        Order order = new Order();
        order.setStatus(OrderStatus.PENDING);

        assertDoesNotThrow(() -> validator.validateTransition(order, OrderStatus.CANCELLED, user));
    }

    @Test
    void validateTransition_Customer_Cancel_Fail_WrongStatus() {
        User user = new User("test@test.com", "hash", Role.CUSTOMER);
        Order order = new Order();
        order.setStatus(OrderStatus.SHIPPED);

        assertThrows(
                InvalidOrderStatusTransitionException.class,
                () -> validator.validateTransition(order, OrderStatus.CANCELLED, user));
    }

    @Test
    void validateTransition_Customer_NotCancel_Fail() {
        User user = new User("test@test.com", "hash", Role.CUSTOMER);
        Order order = new Order();
        order.setStatus(OrderStatus.PENDING);

        assertThrows(
                InvalidOrderStatusTransitionException.class,
                () -> validator.validateTransition(order, OrderStatus.SHIPPED, user));
    }

    @Test
    void validateTransition_Admin_Ship_Success() {
        User admin = new User("admin@test.com", "hash", Role.ADMIN);
        Order order = new Order();
        order.setStatus(OrderStatus.PROCESSING);

        assertDoesNotThrow(() -> validator.validateTransition(order, OrderStatus.SHIPPED, admin));
    }

    @Test
    void validateTransition_Admin_Pending_Fail() {
        User admin = new User("admin@test.com", "hash", Role.ADMIN);
        Order order = new Order();
        order.setStatus(OrderStatus.PROCESSING);

        assertThrows(
                InvalidOrderStatusTransitionException.class,
                () -> validator.validateTransition(order, OrderStatus.PENDING, admin));
    }

    @Test
    void validateTransition_Admin_DeliveredToShipped_Fail() {
        User admin = new User("admin@test.com", "hash", Role.ADMIN);
        Order order = new Order();
        order.setStatus(OrderStatus.DELIVERED);

        assertThrows(
                InvalidOrderStatusTransitionException.class,
                () -> validator.validateTransition(order, OrderStatus.SHIPPED, admin));
    }
}
