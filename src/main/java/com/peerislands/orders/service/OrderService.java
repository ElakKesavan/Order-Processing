package com.peerislands.orders.service;

import com.peerislands.orders.model.*;
import com.peerislands.orders.payload.request.OrderFilter;
import com.peerislands.orders.payload.request.OrderItemRequest;
import com.peerislands.orders.payload.request.OrderRequest;
import com.peerislands.orders.repository.OrderRepository;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private MockInventoryService inventoryService;

    @Autowired
    private MockPaymentService paymentService;

    @Transactional
    public Order createOrder(User user, OrderRequest request) {
        BigDecimal totalAmount = BigDecimal.ZERO;
        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);

        for (OrderItemRequest itemReq : request.getItems()) {
            MockInventoryService.InventoryStatus inventoryStatus = inventoryService.checkAndUpdateInventory(
                    itemReq.getProductId(), itemReq.getQuantity());

            if (inventoryStatus != MockInventoryService.InventoryStatus.SUCCESS) {
                throw new IllegalStateException(
                        "Order failed due to inventory status: "
                                + inventoryStatus
                                + " for product "
                                + itemReq.getProductId());
            }

            BigDecimal itemTotal = itemReq.getPrice().multiply(BigDecimal.valueOf(itemReq.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);

            OrderItem item = new OrderItem(
                    itemReq.getProductId(), itemReq.getQuantity(), itemReq.getPrice());
            order.addItem(item);
        }

        order.setTotalAmount(totalAmount);

        boolean paymentSuccess = paymentService.processPayment(user.getId(), totalAmount);
        if (!paymentSuccess) {
            throw new IllegalStateException("Payment authorization failed for order.");
        }

        return orderRepository.save(order);
    }

    public Page<Order> getCustomerOrders(User user, OrderFilter filter, int page, int size, String sortBy) {
        Pageable pageable = createPageable(page, size, sortBy);
        OrderStatus status = (filter != null) ? filter.getStatus() : null;
        if (status != null) {
            return orderRepository.findByUserAndStatus(user, status, pageable);
        }
        return orderRepository.findByUser(user, pageable);
    }

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
        if (sortBy != null) {
            switch (sortBy.toLowerCase().replace(" ", "")) {
                case "orderid":
                    sort = Sort.by(Sort.Direction.ASC, "id");
                    break;
                case "customerid":
                    sort = Sort.by(Sort.Direction.ASC, "user.id");
                    break;
                case "createdat":
                    sort = Sort.by(Sort.Direction.DESC, "createdAt");
                    break;
                case "updatedat":
                    sort = Sort.by(Sort.Direction.DESC, "updatedAt");
                    break;
            }
        }

        if (sort.isUnsorted()) {
            sort = Sort.by(Sort.Direction.DESC, "updatedAt");
        }

        return PageRequest.of(page, size, sort);
    }

    public Order getOrderById(Long orderId, User user) {
        Order order = orderRepository
                .findById(orderId)
                .orElseThrow(
                        () -> new java.util.NoSuchElementException(
                                "Order not found: " + orderId));

        if (user.getRole() == Role.CUSTOMER && !order.getUser().getId().equals(user.getId())) {
            throw new SecurityException("Unauthorized access to order.");
        }

        return order;
    }

    @Transactional
    public void updateOrderStatus(Long orderId, OrderStatus newStatus, User actor) {
        Order order = orderRepository
                .findById(orderId)
                .orElseThrow(
                        () -> new java.util.NoSuchElementException(
                                "Order not found: " + orderId));

        // 1. Authorization check
        if (actor.getRole() == Role.CUSTOMER && !order.getUser().getId().equals(actor.getId())) {
            throw new SecurityException("You can only update your own orders.");
        }

        // 2. Role-specific constraints
        if (actor.getRole() == Role.CUSTOMER) {
            if (newStatus != OrderStatus.CANCELLED) {
                throw new IllegalArgumentException(
                        "Customers can only update status to CANCELLED.");
            }
            if (order.getStatus() != OrderStatus.PENDING) {
                throw new IllegalStateException(
                        "Orders can only be cancelled while in PENDING status.");
            }
        } else if (actor.getRole() == Role.ADMIN) {
            // Admin specific rules
            if (newStatus == OrderStatus.PENDING) {
                throw new IllegalArgumentException("Cannot manually reset an order to PENDING.");
            }
            // Logic to prevent moving backwards after shipping
            if (order.getStatus() == OrderStatus.DELIVERED && newStatus == OrderStatus.SHIPPED) {
                throw new IllegalStateException("Cannot move a delivered order back to SHIPPED.");
            }
        }

        order.setStatus(newStatus);
        orderRepository.save(order);
    }
}
