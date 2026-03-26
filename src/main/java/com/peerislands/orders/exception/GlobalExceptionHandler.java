package com.peerislands.orders.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(OrderNotFoundException.class)
    public ProblemDetail handleOrderNotFound(OrderNotFoundException ex) {
        return ProblemDetailFactory.create(
                HttpStatus.NOT_FOUND, "Order Not Found", ex.getMessage());
    }

    @ExceptionHandler(UnauthorizedOrderAccessException.class)
    public ProblemDetail handleUnauthorizedAccess(UnauthorizedOrderAccessException ex) {
        return ProblemDetailFactory.create(
                HttpStatus.FORBIDDEN, "Unauthorized Access", ex.getMessage());
    }

    @ExceptionHandler(InventoryUnavailableException.class)
    public ProblemDetail handleInventoryUnavailable(InventoryUnavailableException ex) {
        return ProblemDetailFactory.create(
                HttpStatus.UNPROCESSABLE_ENTITY, "Inventory Unavailable", ex.getMessage());
    }

    @ExceptionHandler(PaymentFailedException.class)
    public ProblemDetail handlePaymentFailed(PaymentFailedException ex) {
        return ProblemDetailFactory.create(
                HttpStatus.PAYMENT_REQUIRED, "Payment Failed", ex.getMessage());
    }

    @ExceptionHandler(InvalidOrderStatusTransitionException.class)
    public ProblemDetail handleInvalidTransition(InvalidOrderStatusTransitionException ex) {
        return ProblemDetailFactory.create(
                HttpStatus.CONFLICT, "Invalid Order Status Transition", ex.getMessage());
    }

    @ExceptionHandler(OrderProcessingException.class)
    public ProblemDetail handleGenericOrderProcessingException(OrderProcessingException ex) {
        return ProblemDetailFactory.create(
                HttpStatus.BAD_REQUEST, "Order Processing Error", ex.getMessage());
    }

    @ExceptionHandler(AuthenticationException.class)
    public ProblemDetail handleAuthenticationException(AuthenticationException ex) {
        return ProblemDetailFactory.create(
                HttpStatus.UNAUTHORIZED, "Authentication Failed", ex.getMessage());
    }

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
