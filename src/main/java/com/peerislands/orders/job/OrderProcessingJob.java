package com.peerislands.orders.job;

import com.peerislands.orders.model.Order;
import com.peerislands.orders.model.OrderStatus;
import com.peerislands.orders.repository.OrderRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class OrderProcessingJob {
    private static final Logger logger = LoggerFactory.getLogger(OrderProcessingJob.class);

    private final OrderRepository orderRepository;

    @Scheduled(fixedRate = 300000) // 5 minutes
    @Transactional
    public void processPendingOrders() {
        LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(5);
        List<Order> pendingOrders =
                orderRepository.findByStatusAndCreatedAtBefore(OrderStatus.PENDING, cutoffTime);

        if (!pendingOrders.isEmpty()) {
            logger.info(
                    "OrderProcessingJob: Found {} pending orders older than 5 minutes. Processing now...",
                    pendingOrders.size());
            for (Order order : pendingOrders) {
                order.setStatus(OrderStatus.PROCESSING);
                logger.info(
                        "OrderProcessingJob: Order {} transitioned to PROCESSING.", order.getId());
            }
            orderRepository.saveAll(pendingOrders);
        }
    }
}
