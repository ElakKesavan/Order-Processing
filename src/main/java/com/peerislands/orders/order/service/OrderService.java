package com.peerislands.orders.order.service;

import com.peerislands.orders.auth.model.User;
import com.peerislands.orders.order.model.Order;
import com.peerislands.orders.order.model.OrderStatus;
import com.peerislands.orders.order.payload.request.OrderFilter;
import com.peerislands.orders.order.payload.request.OrderRequest;
import org.springframework.data.domain.Page;

public interface OrderService {
    Order createOrder(User user, OrderRequest request);

    Page<Order> getOrders(User user, OrderFilter filter, int page, int size, String sortBy);

    Page<Order> getCustomerOrders(User user, OrderFilter filter, int page, int size, String sortBy);

    Page<Order> getAllOrders(OrderFilter filter, int page, int size, String sortBy);

    Order getOrderById(Long orderId, User user);

    void updateOrderStatus(Long orderId, OrderStatus newStatus, User actor);
}
