package com.peerislands.orders.order.repository;

import com.peerislands.orders.auth.model.User;
import com.peerislands.orders.order.model.Order;
import com.peerislands.orders.order.model.OrderStatus;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // For customers retrieving their own orders
    Page<Order> findByUser(User user, Pageable pageable);

    Page<Order> findByUserAndStatus(User user, OrderStatus status, Pageable pageable);

    // For admins retrieving paginated orders optionally filtered
    Page<Order> findByStatus(OrderStatus status, Pageable pageable);

    // For scheduled jobs: PENDING orders older than a specified time
    List<Order> findByStatusAndCreatedAtBefore(OrderStatus status, LocalDateTime time);
}
