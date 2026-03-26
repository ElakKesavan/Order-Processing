package com.peerislands.orders.order.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.peerislands.orders.auth.model.Role;
import com.peerislands.orders.auth.model.User;
import com.peerislands.orders.order.model.Order;
import com.peerislands.orders.order.model.OrderStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@DataJpaTest
public class OrderRepositoryTest {

    @Autowired private TestEntityManager entityManager;

    @Autowired private OrderRepository orderRepository;

    @Test
    void findByStatusAndCreatedAtBefore() {
        User user = new User("test@repo.com", "hash", Role.CUSTOMER);
        user = entityManager.persistAndFlush(user);

        Order recentOrder = new Order();
        recentOrder.setUser(user);
        recentOrder.setStatus(OrderStatus.PENDING);
        recentOrder.setTotalAmount(new BigDecimal("10.00"));
        recentOrder.setCreatedAt(LocalDateTime.now().minusMinutes(2));
        recentOrder.setUpdatedAt(LocalDateTime.now().minusMinutes(2));
        entityManager.persist(recentOrder);

        Order oldOrder = new Order();
        oldOrder.setUser(user);
        oldOrder.setStatus(OrderStatus.PENDING);
        oldOrder.setTotalAmount(new BigDecimal("20.00"));
        oldOrder.setCreatedAt(LocalDateTime.now().minusMinutes(10));
        oldOrder.setUpdatedAt(LocalDateTime.now().minusMinutes(10));
        entityManager.persist(oldOrder);

        entityManager.flush();

        List<Order> found =
                orderRepository.findByStatusAndCreatedAtBefore(
                        OrderStatus.PENDING, LocalDateTime.now().minusMinutes(5));

        assertEquals(1, found.size());
        assertEquals(oldOrder.getId(), found.get(0).getId());
    }

    @Test
    void findByUserAndStatus() {
        User user = new User("test2@repo.com", "hash", Role.CUSTOMER);
        user = entityManager.persistAndFlush(user);

        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.SHIPPED);
        order.setTotalAmount(new BigDecimal("10.00"));
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        entityManager.persistAndFlush(order);

        Page<Order> results =
                orderRepository.findByUserAndStatus(
                        user, OrderStatus.SHIPPED, PageRequest.of(0, 10));
        assertEquals(1, results.getTotalElements());
    }
}
