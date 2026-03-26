package com.peerislands.orders.service;

import com.peerislands.orders.model.Order;
import com.peerislands.orders.model.OrderStatus;
import com.peerislands.orders.model.User;
import com.peerislands.orders.payload.request.OrderFilter;
import com.peerislands.orders.payload.request.OrderRequest;
import org.springframework.data.domain.Page;

public interface OrderService {
    Order createOrder(User user, OrderRequest request);

    Page<Order> getOrders(User user, OrderFilter filter, int page, int size, String sortBy);

    Page<Order> getCustomerOrders(User user, OrderFilter filter, int page, int size, String sortBy);

    Page<Order> getAllOrders(OrderFilter filter, int page, int size, String sortBy);

    Order getOrderById(Long orderId, User user);

    void updateOrderStatus(Long orderId, OrderStatus newStatus, User actor);
}
