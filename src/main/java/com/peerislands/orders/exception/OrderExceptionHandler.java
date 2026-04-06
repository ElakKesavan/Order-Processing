package com.peerislands.orders.exception;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Order(2)
@RestControllerAdvice
public class OrderExceptionHandler {

    @ExceptionHandler(OrderNotFoundException.class)
    public ProblemDetail handleOrderNotFound(OrderNotFoundException ex) {
        return ProblemDetailFactory.create(
                HttpStatus.NOT_FOUND, "Order Not Found", ex.getMessage());
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
}
