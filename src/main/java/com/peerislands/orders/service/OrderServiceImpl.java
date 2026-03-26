package com.peerislands.orders.service;

import com.peerislands.orders.exception.OrderNotFoundException;
import com.peerislands.orders.model.Order;
import com.peerislands.orders.model.OrderStatus;
import com.peerislands.orders.model.Role;
import com.peerislands.orders.model.User;
import com.peerislands.orders.payload.request.OrderFilter;
import com.peerislands.orders.payload.request.OrderRequest;
import com.peerislands.orders.repository.OrderRepository;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class OrderServiceImpl implements OrderService {

    private static final Map<String, Sort> SORT_MAPPINGS =
            Map.of(
                    "orderid", Sort.by(Sort.Direction.ASC, "id"),
                    "customerid", Sort.by(Sort.Direction.ASC, "user.id"),
                    "userid", Sort.by(Sort.Direction.ASC, "user.id"),
                    "createdat", Sort.by(Sort.Direction.DESC, "createdAt"),
                    "updatedat", Sort.by(Sort.Direction.DESC, "updatedAt"));

    @Autowired private OrderRepository orderRepository;

    @Autowired private OrderStatusValidator orderStatusValidator;

    @Autowired private OrderAuthorizationService orderAuthorizationService;

    @Autowired private OrderOrchestrator orderOrchestrator;

    @Transactional
    public Order createOrder(User user, OrderRequest request) {
        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);

        orderOrchestrator.orchestrate(user, order, request);

        return orderRepository.save(order);
    }

    @Transactional(readOnly = true)
    public Page<Order> getOrders(User user, OrderFilter filter, int page, int size, String sortBy) {
        if (user.getRole() == Role.ADMIN) {
            return getAllOrders(filter, page, size, sortBy);
        }
        return getCustomerOrders(user, filter, page, size, sortBy);
    }

    @Transactional(readOnly = true)
    public Page<Order> getCustomerOrders(
            User user, OrderFilter filter, int page, int size, String sortBy) {
        Pageable pageable = createPageable(page, size, sortBy);
        OrderStatus status = (filter != null) ? filter.getStatus() : null;
        if (status != null) {
            return orderRepository.findByUserAndStatus(user, status, pageable);
        }
        return orderRepository.findByUser(user, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Order> getAllOrders(OrderFilter filter, int page, int size, String sortBy) {
        Pageable pageable = createPageable(page, size, sortBy);
        OrderStatus status = (filter != null) ? filter.getStatus() : null;
        if (status != null) {
            return orderRepository.findByStatus(status, pageable);
        }
        return orderRepository.findAll(pageable);
    }

    private Pageable createPageable(int page, int size, String sortBy) {
        Sort sort = Sort.unsorted();
        if (StringUtils.hasText(sortBy)) {
            String sanitizedSort = sortBy.toLowerCase().replaceAll("[\\s_-]", "");
            sort = SORT_MAPPINGS.getOrDefault(sanitizedSort, Sort.unsorted());
        }

        if (sort.isUnsorted()) {
            sort = Sort.by(Sort.Direction.DESC, "updatedAt");
        }

        return PageRequest.of(page, size, sort);
    }

    @Transactional(readOnly = true)
    public Order getOrderById(Long orderId, User user) {
        Order order =
                orderRepository
                        .findById(orderId)
                        .orElseThrow(
                                () -> new OrderNotFoundException("Order not found: " + orderId));

        orderAuthorizationService.validateOrderAccess(order, user);

        return order;
    }

    @Transactional
    public void updateOrderStatus(Long orderId, OrderStatus newStatus, User actor) {
        Order order =
                orderRepository
                        .findById(orderId)
                        .orElseThrow(
                                () -> new OrderNotFoundException("Order not found: " + orderId));

        // 1. Authorization check
        orderAuthorizationService.validateOrderUpdateAccess(order, actor);

        // 2. State transition validation
        orderStatusValidator.validateTransition(order, newStatus, actor);

        order.setStatus(newStatus);
        orderRepository.save(order);
    }
}
