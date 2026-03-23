package com.peerislands.orders.controller;

import com.peerislands.orders.mapper.OrderMapper;
import java.util.NoSuchElementException;
import com.peerislands.orders.model.Order;
import com.peerislands.orders.model.OrderStatus;
import com.peerislands.orders.model.Role;
import com.peerislands.orders.model.User;
import com.peerislands.orders.payload.request.OrderRequest;
import com.peerislands.orders.payload.request.UpdateOrderStatusRequest;
import com.peerislands.orders.payload.response.MessageResponse;
import com.peerislands.orders.payload.response.OrderResponse;
import com.peerislands.orders.security.services.AuthenticationHelper;
import com.peerislands.orders.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private AuthenticationHelper authenticationHelper;

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> createOrder(
            @Valid @RequestBody OrderRequest orderRequest, Authentication authentication) {
        try {
            User user = authenticationHelper.getAuthenticatedUser(authentication);
            Order createdOrder = orderService.createOrder(user, orderRequest);
            return ResponseEntity.ok(orderMapper.toOrderResponse(createdOrder));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<Page<OrderResponse>> getOrders(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {

        User user = authenticationHelper.getAuthenticatedUser(authentication);
        Pageable pageable = PageRequest.of(page, size);

        Page<Order> orders;
        if (user.getRole() == Role.ADMIN) {
            orders = orderService.getAllOrders(status, pageable);
        } else {
            orders = orderService.getCustomerOrders(user, status, pageable);
        }
        return ResponseEntity.ok(orders.map(orderMapper::toOrderResponse));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable Long id, Authentication authentication) {
        try {
            User user = authenticationHelper.getAuthenticatedUser(authentication);
            Order order = orderService.getOrderById(id, user);
            return ResponseEntity.ok(orderMapper.toOrderResponse(order));
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(new MessageResponse(e.getMessage()));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateOrderStatusRequest request,
            Authentication authentication) {
        try {
            User user = authenticationHelper.getAuthenticatedUser(authentication);
            orderService.updateOrderStatus(id, request.getStatus(), user);
            return ResponseEntity.ok(new MessageResponse("Order status updated to " + request.getStatus()));
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(new MessageResponse(e.getMessage()));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).body(new MessageResponse(e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
}
