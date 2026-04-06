package com.peerislands.orders.exception;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Order(1)
@RestControllerAdvice
public class AuthExceptionHandler {

    @ExceptionHandler(AuthenticationException.class)
    public ProblemDetail handleAuthenticationException(AuthenticationException ex) {
        return ProblemDetailFactory.create(
                HttpStatus.UNAUTHORIZED, "Authentication Failed", ex.getMessage());
    }

    @ExceptionHandler(UnauthorizedOrderAccessException.class)
    public ProblemDetail handleUnauthorizedAccess(UnauthorizedOrderAccessException ex) {
        return ProblemDetailFactory.create(
                HttpStatus.FORBIDDEN, "Unauthorized Access", ex.getMessage());
    }
}
