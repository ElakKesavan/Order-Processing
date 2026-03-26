package com.peerislands.orders.order.service;

import com.peerislands.orders.auth.model.User;
import com.peerislands.orders.inventory.exception.InventoryUnavailableException;
import com.peerislands.orders.inventory.service.InventoryService;
import com.peerislands.orders.order.model.Order;
import com.peerislands.orders.order.model.OrderItem;
import com.peerislands.orders.order.payload.request.OrderItemRequest;
import com.peerislands.orders.order.payload.request.OrderRequest;
import com.peerislands.orders.payment.exception.PaymentFailedException;
import com.peerislands.orders.payment.service.PaymentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderOrchestrator {

    private final InventoryService inventoryService;
    private final PaymentService paymentService;
    private final OrderPricingCalculator pricingCalculator;

    public void orchestrate(
            @NotNull User user, @NotNull Order order, @NotNull @Valid OrderRequest request) {
        BigDecimal totalAmount = pricingCalculator.calculateTotalAmount(request);
        order.setTotalAmount(totalAmount);

        for (OrderItemRequest itemReq : request.getItems()) {
            InventoryService.InventoryStatus inventoryStatus =
                    inventoryService.checkAndUpdateInventory(
                            itemReq.getProductId(), itemReq.getQuantity());

            if (inventoryStatus != InventoryService.InventoryStatus.SUCCESS) {
                throw new InventoryUnavailableException(
                        "Order failed due to inventory status: "
                                + inventoryStatus
                                + " for product "
                                + itemReq.getProductId());
            }

            OrderItem item =
                    new OrderItem(
                            itemReq.getProductId(), itemReq.getQuantity(), itemReq.getPrice());
            order.addItem(item);
        }

        boolean paymentSuccess = paymentService.processPayment(user.getId(), totalAmount);
        if (!paymentSuccess) {
            throw new PaymentFailedException("Payment authorization failed for order.");
        }
    }
}
