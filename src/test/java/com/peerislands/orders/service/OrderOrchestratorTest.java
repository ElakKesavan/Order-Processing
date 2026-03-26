package com.peerislands.orders.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.peerislands.orders.exception.InventoryUnavailableException;
import com.peerislands.orders.exception.PaymentFailedException;
import com.peerislands.orders.model.Order;
import com.peerislands.orders.model.User;
import com.peerislands.orders.payload.request.OrderItemRequest;
import com.peerislands.orders.payload.request.OrderRequest;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class OrderOrchestratorTest {

    @Mock private InventoryService inventoryService;
    @Mock private PaymentService paymentService;
    @Mock private OrderPricingCalculator pricingCalculator;

    @InjectMocks private OrderOrchestrator orchestrator;

    @Test
    void orchestrate_Success() {
        User user = new User();
        user.setId(1L);
        Order order = new Order();
        OrderRequest request = new OrderRequest();
        OrderItemRequest itemReq = new OrderItemRequest();
        itemReq.setProductId("PROD1");
        itemReq.setQuantity(2);
        itemReq.setPrice(new BigDecimal("10.00"));
        request.setItems(List.of(itemReq));

        when(pricingCalculator.calculateTotalAmount(request)).thenReturn(new BigDecimal("20.00"));
        when(inventoryService.checkAndUpdateInventory("PROD1", 2))
                .thenReturn(InventoryService.InventoryStatus.SUCCESS);
        when(paymentService.processPayment(1L, new BigDecimal("20.00"))).thenReturn(true);

        orchestrator.orchestrate(user, order, request);

        assertEquals(new BigDecimal("20.00"), order.getTotalAmount());
        assertEquals(1, order.getItems().size());
        verify(inventoryService).checkAndUpdateInventory("PROD1", 2);
        verify(paymentService).processPayment(1L, new BigDecimal("20.00"));
    }

    @Test
    void orchestrate_InventoryFail() {
        User user = new User();
        Order order = new Order();
        OrderRequest request = new OrderRequest();
        OrderItemRequest itemReq = new OrderItemRequest();
        itemReq.setProductId("PROD1");
        itemReq.setQuantity(2);
        request.setItems(List.of(itemReq));

        when(pricingCalculator.calculateTotalAmount(request)).thenReturn(new BigDecimal("20.00"));
        when(inventoryService.checkAndUpdateInventory("PROD1", 2))
                .thenReturn(InventoryService.InventoryStatus.INSUFFICIENT_STOCK);

        assertThrows(
                InventoryUnavailableException.class,
                () -> orchestrator.orchestrate(user, order, request));
    }

    @Test
    void orchestrate_PaymentFail() {
        User user = new User();
        user.setId(1L);
        Order order = new Order();
        OrderRequest request = new OrderRequest();
        OrderItemRequest itemReq = new OrderItemRequest();
        itemReq.setProductId("PROD1");
        itemReq.setQuantity(2);
        request.setItems(List.of(itemReq));

        when(pricingCalculator.calculateTotalAmount(request)).thenReturn(new BigDecimal("20.00"));
        when(inventoryService.checkAndUpdateInventory("PROD1", 2))
                .thenReturn(InventoryService.InventoryStatus.SUCCESS);
        when(paymentService.processPayment(any(), any())).thenReturn(false);

        assertThrows(
                PaymentFailedException.class, () -> orchestrator.orchestrate(user, order, request));
    }
}
