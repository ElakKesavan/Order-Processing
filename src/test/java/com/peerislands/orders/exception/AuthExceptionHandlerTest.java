package com.peerislands.orders.exception;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

public class AuthExceptionHandlerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc =
                MockMvcBuilders.standaloneSetup(new ExceptionTriggerController())
                        .setControllerAdvice(new AuthExceptionHandler())
                        .build();
    }

    @RestController
    static class ExceptionTriggerController {
        @GetMapping("/test/bad-credentials")
        public void throwBadCredentials() {
            throw new BadCredentialsException("Invalid login");
        }

        @GetMapping("/test/unauthorized-access")
        public void throwUnauthorizedAccess() {
            throw new UnauthorizedOrderAccessException("Cannot access order 123");
        }
    }

    @Test
    void handleAuthenticationException_Returns401() throws Exception {
        mockMvc.perform(get("/test/bad-credentials"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.title").value("Authentication Failed"))
                .andExpect(jsonPath("$.detail").value("Invalid login"));
    }

    @Test
    void handleUnauthorizedOrderAccess_Returns403() throws Exception {
        mockMvc.perform(get("/test/unauthorized-access"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.title").value("Unauthorized Access"))
                .andExpect(jsonPath("$.detail").value("Cannot access order 123"));
    }
}
