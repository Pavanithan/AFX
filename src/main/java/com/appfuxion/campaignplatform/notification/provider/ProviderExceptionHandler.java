package com.appfuxion.campaignplatform.notification.provider;

import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** Kept local to this package, not in the shared advice, so common/ has no reason to know about it. */
@RestControllerAdvice
public class ProviderExceptionHandler {

    public record ErrorResponse(Instant timestamp, int status, String error, String message) {}

    @ExceptionHandler(ProviderRateLimitException.class)
    public ResponseEntity<ErrorResponse> handleRateLimit(ProviderRateLimitException ex) {
        return build(HttpStatus.TOO_MANY_REQUESTS, ex.getMessage());
    }

    @ExceptionHandler(ProviderUnavailableException.class)
    public ResponseEntity<ErrorResponse> handleUnavailable(ProviderUnavailableException ex) {
        return build(HttpStatus.BAD_GATEWAY, ex.getMessage());
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String message) {
        return ResponseEntity.status(status)
                .body(new ErrorResponse(Instant.now(), status.value(), status.getReasonPhrase(), message));
    }
}
