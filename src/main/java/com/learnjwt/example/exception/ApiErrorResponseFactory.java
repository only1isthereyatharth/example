package com.learnjwt.example.exception;

import java.time.Instant;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;

@Component
public class ApiErrorResponseFactory {

    public ApiErrorResponse create(HttpStatusCode status, String message, String path) {
        return create(status, message, path, Map.of());
    }

    public ApiErrorResponse create(HttpStatusCode status, String message, String path, Map<String, String> validationErrors) {
        HttpStatus httpStatus = HttpStatus.resolve(status.value());
        return new ApiErrorResponse(
            Instant.now(),
            status.value(),
            httpStatus != null ? httpStatus.getReasonPhrase() : "Unexpected Error",
            message,
            path,
            validationErrors
        );
    }
}
