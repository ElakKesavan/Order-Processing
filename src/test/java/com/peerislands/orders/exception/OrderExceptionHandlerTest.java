package com.peerislands.orders.exception;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

public class OrderExceptionHandlerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc =
                MockMvcBuilders.standaloneSetup(new ExceptionTriggerController())
                        .setControllerAdvice(new OrderExceptionHandler())
                        .build();
    }

    @RestController
    static class ExceptionTriggerController {
        @GetMapping("/test/order-not-found")
        public void throwOrderNotFound() {
            throw new OrderNotFoundException("Order 123 not found");
        }

        @GetMapping("/test/inventory-unavailable")
        public void throwInventoryUnavailable() {
            throw new InventoryUnavailableException("Item out of stock");
        }

        @GetMapping("/test/payment-failed")
        public void throwPaymentFailed() {
            throw new PaymentFailedException("Card declined");
        }

        @GetMapping("/test/invalid-transition")
        public void throwInvalidTransition() {
            throw new InvalidOrderStatusTransitionException(
                    "Cannot transition from SHIPPED to PENDING");
        }

        @GetMapping("/test/order-processing-error")
        public void throwOrderProcessingError() {
            throw new OrderProcessingException("Failed to process order");
        }
    }

    @Test
    void handleOrderNotFound_Returns404() throws Exception {
        mockMvc.perform(get("/test/order-not-found"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Order Not Found"))
                .andExpect(jsonPath("$.detail").value("Order 123 not found"));
    }

    @Test
    void handleInventoryUnavailable_Returns422() throws Exception {
        mockMvc.perform(get("/test/inventory-unavailable"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.title").value("Inventory Unavailable"))
                .andExpect(jsonPath("$.detail").value("Item out of stock"));
    }

    @Test
    void handlePaymentFailed_Returns402() throws Exception {
        mockMvc.perform(get("/test/payment-failed"))
                .andExpect(status().isPaymentRequired())
                .andExpect(jsonPath("$.title").value("Payment Failed"))
                .andExpect(jsonPath("$.detail").value("Card declined"));
    }

    @Test
    void handleInvalidOrderStatusTransition_Returns409() throws Exception {
        mockMvc.perform(get("/test/invalid-transition"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Invalid Order Status Transition"))
                .andExpect(jsonPath("$.detail").value("Cannot transition from SHIPPED to PENDING"));
    }

    @Test
    void handleOrderProcessingException_Returns400() throws Exception {
        mockMvc.perform(get("/test/order-processing-error"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Order Processing Error"))
                .andExpect(jsonPath("$.detail").value("Failed to process order"));
    }
}
