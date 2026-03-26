package com.peerislands.orders.core.exception;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.peerislands.orders.inventory.exception.InventoryUnavailableException;
import com.peerislands.orders.order.exception.InvalidOrderStatusTransitionException;
import com.peerislands.orders.order.exception.OrderNotFoundException;
import com.peerislands.orders.order.exception.UnauthorizedOrderAccessException;
import com.peerislands.orders.payment.exception.PaymentFailedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

public class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc =
                MockMvcBuilders.standaloneSetup(new ExceptionTriggerController())
                        .setControllerAdvice(new GlobalExceptionHandler())
                        .build();
    }

    @RestController
    static class ExceptionTriggerController {
        @GetMapping("/test/order-not-found")
        public void throwOrderNotFound() {
            throw new OrderNotFoundException("Order 123 not found");
        }

        @GetMapping("/test/unauthorized-access")
        public void throwUnauthorizedAccess() {
            throw new UnauthorizedOrderAccessException("Cannot access order 123");
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

        @GetMapping("/test/bad-credentials")
        public void throwBadCredentials() {
            throw new BadCredentialsException("Invalid login");
        }

        @GetMapping("/test/illegal-argument")
        public void throwIllegalArgument() {
            throw new IllegalArgumentException("Invalid input");
        }

        @GetMapping("/test/generic-exception")
        public void throwGenericException() throws Exception {
            throw new Exception("Unexpected error");
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
    void handleUnauthorizedOrderAccess_Returns403() throws Exception {
        mockMvc.perform(get("/test/unauthorized-access"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.title").value("Unauthorized Access"))
                .andExpect(jsonPath("$.detail").value("Cannot access order 123"));
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
                .andExpect(jsonPath("$.title").value("Invalid Order Status Transition"));
    }

    @Test
    void handleAuthenticationException_Returns401() throws Exception {
        mockMvc.perform(get("/test/bad-credentials"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.title").value("Authentication Failed"))
                .andExpect(jsonPath("$.detail").value("Invalid login"));
    }

    @Test
    void handleIllegalArgumentException_Returns400() throws Exception {
        mockMvc.perform(get("/test/illegal-argument"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Invalid Request"))
                .andExpect(jsonPath("$.detail").value("Invalid input"));
    }

    @Test
    void handleGlobalException_Returns500() throws Exception {
        mockMvc.perform(get("/test/generic-exception"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.title").value("Internal Server Error"))
                .andExpect(jsonPath("$.detail").value("An unexpected error occurred"));
    }
}
