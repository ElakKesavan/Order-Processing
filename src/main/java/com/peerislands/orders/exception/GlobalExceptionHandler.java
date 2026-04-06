package com.peerislands.orders.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Order(Ordered.LOWEST_PRECEDENCE)
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgumentException(IllegalArgumentException ex) {
        return ProblemDetailFactory.create(
                HttpStatus.BAD_REQUEST, "Invalid Request", ex.getMessage());
    }

    @ExceptionHandler(jakarta.validation.ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolationException(
            jakarta.validation.ConstraintViolationException ex) {
        String detail =
                ex.getConstraintViolations().stream()
                        .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                        .collect(java.util.stream.Collectors.joining(", "));
        return ProblemDetailFactory.create(HttpStatus.BAD_REQUEST, "Validation Error", detail);
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGlobalException(Exception ex) {
        log.error("Unhandled exception caught: ", ex);
        return ProblemDetailFactory.create(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal Server Error",
                "An unexpected error occurred");
    }
}
