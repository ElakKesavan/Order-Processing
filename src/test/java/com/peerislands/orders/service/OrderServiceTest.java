package com.peerislands.orders.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.peerislands.orders.model.*;
import com.peerislands.orders.payload.request.OrderItemRequest;
import com.peerislands.orders.payload.request.OrderRequest;
import com.peerislands.orders.repository.OrderRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock private OrderRepository orderRepository;

    @Mock private MockInventoryService inventoryService;

    @Mock private MockPaymentService paymentService;

    @InjectMocks private OrderService orderService;

    @Test
    void createOrder_Success() {
        User user = new User("test@test.com", "hash", Role.CUSTOMER);
        user.setId(1L);

        OrderItemRequest itemReq = new OrderItemRequest();
        itemReq.setProductId("PROD1");
        itemReq.setQuantity(2);
        itemReq.setPrice(new BigDecimal("10.00"));

        OrderRequest request = new OrderRequest();
        request.setItems(List.of(itemReq));

        when(inventoryService.checkAndUpdateInventory("PROD1", 2))
                .thenReturn(MockInventoryService.InventoryStatus.SUCCESS);
        when(paymentService.processPayment(eq(1L), any(BigDecimal.class))).thenReturn(true);
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArguments()[0]);

        Order order = orderService.createOrder(user, request);

        assertNotNull(order);
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(new BigDecimal("20.00"), order.getTotalAmount());
        assertEquals(1, order.getItems().size());

        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void createOrder_InventoryFails() {
        User user = new User("test@test.com", "hash", Role.CUSTOMER);
        user.setId(1L);

        OrderItemRequest itemReq = new OrderItemRequest();
        itemReq.setProductId("PROD1");
        itemReq.setQuantity(2);
        itemReq.setPrice(new BigDecimal("10.00"));

        OrderRequest request = new OrderRequest();
        request.setItems(List.of(itemReq));

        when(inventoryService.checkAndUpdateInventory("PROD1", 2))
                .thenReturn(MockInventoryService.InventoryStatus.INSUFFICIENT_STOCK);

        assertThrows(IllegalStateException.class, () -> orderService.createOrder(user, request));

        verify(paymentService, never()).processPayment(any(), any());
        verify(orderRepository, never()).save(any());
    }

    @Test
    void cancelOrder_Success() {
        User user = new User("test@test.com", "hash", Role.CUSTOMER);
        user.setId(1L);

        Order order = new Order();
        order.setId(10L);
        order.setStatus(OrderStatus.PENDING);
        order.setUser(user);

        when(orderRepository.findById(10L)).thenReturn(Optional.of(order));

        orderService.cancelOrder(10L, user);

        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        verify(orderRepository).save(order);
    }

    @Test
    void cancelOrder_UnauthorizedUser() {
        User owner = new User("owner@test.com", "hash", Role.CUSTOMER);
        owner.setId(1L);

        User otherUser = new User("other@test.com", "hash", Role.CUSTOMER);
        otherUser.setId(2L);

        Order order = new Order();
        order.setId(10L);
        order.setStatus(OrderStatus.PENDING);
        order.setUser(owner);

        when(orderRepository.findById(10L)).thenReturn(Optional.of(order));

        assertThrows(SecurityException.class, () -> orderService.cancelOrder(10L, otherUser));
    }

    @Test
    void cancelOrder_WrongStatus() {
        User user = new User("test@test.com", "hash", Role.CUSTOMER);
        user.setId(1L);

        Order order = new Order();
        order.setId(10L);
        order.setStatus(OrderStatus.SHIPPED);
        order.setUser(user);

        when(orderRepository.findById(10L)).thenReturn(Optional.of(order));

        assertThrows(IllegalStateException.class, () -> orderService.cancelOrder(10L, user));
    }
}
