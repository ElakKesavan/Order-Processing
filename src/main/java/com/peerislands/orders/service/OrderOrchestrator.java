package com.peerislands.orders.service;

import com.peerislands.orders.exception.InventoryUnavailableException;
import com.peerislands.orders.exception.PaymentFailedException;
import com.peerislands.orders.model.Order;
import com.peerislands.orders.model.OrderItem;
import com.peerislands.orders.model.User;
import com.peerislands.orders.payload.request.OrderItemRequest;
import com.peerislands.orders.payload.request.OrderRequest;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderOrchestrator {

    @Autowired private InventoryService inventoryService;
    @Autowired private PaymentService paymentService;
    @Autowired private OrderPricingCalculator pricingCalculator;

    public void orchestrate(User user, Order order, OrderRequest request) {
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
