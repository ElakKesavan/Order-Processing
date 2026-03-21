package com.peerislands.orders.service;

import com.peerislands.orders.model.*;
import com.peerislands.orders.payload.request.OrderItemRequest;
import com.peerislands.orders.payload.request.OrderRequest;
import com.peerislands.orders.repository.OrderRepository;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    @Autowired private OrderRepository orderRepository;

    @Autowired private MockInventoryService inventoryService;

    @Autowired private MockPaymentService paymentService;

    @Transactional
    public Order createOrder(User user, OrderRequest request) {
        BigDecimal totalAmount = BigDecimal.ZERO;
        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);

        for (OrderItemRequest itemReq : request.getItems()) {
            MockInventoryService.InventoryStatus inventoryStatus =
                    inventoryService.checkAndUpdateInventory(
                            itemReq.getProductId(), itemReq.getQuantity());

            if (inventoryStatus != MockInventoryService.InventoryStatus.SUCCESS) {
                throw new IllegalStateException(
                        "Order failed due to inventory status: "
                                + inventoryStatus
                                + " for product "
                                + itemReq.getProductId());
            }

            BigDecimal itemTotal =
                    itemReq.getPrice().multiply(BigDecimal.valueOf(itemReq.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);

            OrderItem item =
                    new OrderItem(
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

    public Page<Order> getCustomerOrders(User user, OrderStatus status, Pageable pageable) {
        if (status != null) {
            return orderRepository.findByUserAndStatus(user, status, pageable);
        }
        return orderRepository.findByUser(user, pageable);
    }

    public Page<Order> getAllOrders(OrderStatus status, Pageable pageable) {
        if (status != null) {
            return orderRepository.findByStatus(status, pageable);
        }
        return orderRepository.findAll(pageable);
    }

    public Order getOrderById(Long orderId, User user) {
        Order order =
                orderRepository
                        .findById(orderId)
                        .orElseThrow(
                                () -> new IllegalArgumentException("Order not found: " + orderId));

        if (user.getRole() == Role.CUSTOMER && !order.getUser().getId().equals(user.getId())) {
            throw new SecurityException("Unauthorized access to order.");
        }

        return order;
    }

    @Transactional
    public void cancelOrder(Long orderId, User user) {
        Order order = getOrderById(orderId, user);

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException("Cannot cancel order in status: " + order.getStatus());
        }

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }

    @Transactional
    public void updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order =
                orderRepository
                        .findById(orderId)
                        .orElseThrow(
                                () -> new IllegalArgumentException("Order not found: " + orderId));

        order.setStatus(newStatus);
        orderRepository.save(order);
    }
}
