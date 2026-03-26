package com.peerislands.orders.exception;

import java.net.URI;
import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

/** Utility class for creating ProblemDetail error responses. */
public final class ProblemDetailFactory {

    private ProblemDetailFactory() {
        // Private constructor to prevent instantiation
    }

    /**
     * Creates a ProblemDetail object with the provided status, title, and detail.
     *
     * @param status the HTTP status
     * @param title the error title
     * @param detail the error detail message
     * @return the constructed ProblemDetail
     */
    public static ProblemDetail create(HttpStatus status, String title, String detail) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        problemDetail.setTitle(title);
        problemDetail.setType(URI.create("about:blank"));
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }
}
