package com.peerislands.orders.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.peerislands.orders.exception.UnauthorizedOrderAccessException;
import com.peerislands.orders.model.Order;
import com.peerislands.orders.model.Role;
import com.peerislands.orders.model.User;
import org.junit.jupiter.api.Test;

public class OrderAuthorizationServiceTest {

    private final OrderAuthorizationService authorizationService = new OrderAuthorizationService();

    @Test
    void validateOrderAccess_Admin_Success() {
        User admin = new User("admin@test.com", "hash", Role.ADMIN);
        Order order = new Order();
        User owner = new User("owner@test.com", "hash", Role.CUSTOMER);
        owner.setId(1L);
        order.setUser(owner);

        assertDoesNotThrow(() -> authorizationService.validateOrderAccess(order, admin));
    }

    @Test
    void validateOrderAccess_Owner_Success() {
        User owner = new User("owner@test.com", "hash", Role.CUSTOMER);
        owner.setId(1L);
        Order order = new Order();
        order.setUser(owner);

        assertDoesNotThrow(() -> authorizationService.validateOrderAccess(order, owner));
    }

    @Test
    void validateOrderAccess_NonOwner_Fail() {
        User owner = new User("owner@test.com", "hash", Role.CUSTOMER);
        owner.setId(1L);
        User other = new User("other@test.com", "hash", Role.CUSTOMER);
        other.setId(2L);
        Order order = new Order();
        order.setUser(owner);

        assertThrows(
                UnauthorizedOrderAccessException.class,
                () -> authorizationService.validateOrderAccess(order, other));
    }
}
