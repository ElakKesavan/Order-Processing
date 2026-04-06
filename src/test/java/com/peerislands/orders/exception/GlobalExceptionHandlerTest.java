package com.peerislands.orders.exception;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
        @GetMapping("/test/illegal-argument")
        public void throwIllegalArgument() {
            throw new IllegalArgumentException("Invalid input");
        }

        @GetMapping("/test/generic-exception")
        public void throwGenericException() throws Exception {
            throw new Exception("Unexpected error");
        }

        @GetMapping("/test/constraint-violation")
        public void throwConstraintViolation() {
            ConstraintViolation<?> violation = mock(ConstraintViolation.class);
            Path path = mock(Path.class);
            when(path.toString()).thenReturn("quantity");
            when(violation.getPropertyPath()).thenReturn(path);
            when(violation.getMessage()).thenReturn("must be at most 100");

            throw new ConstraintViolationException(Set.of(violation));
        }
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

    @Test
    void handleConstraintViolationException_Returns400() throws Exception {
        mockMvc.perform(get("/test/constraint-violation"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation Error"))
                .andExpect(jsonPath("$.detail").value("quantity: must be at most 100"));
    }
}
