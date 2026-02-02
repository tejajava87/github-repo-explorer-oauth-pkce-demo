package com.githubrepoexplorerbackend.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(OAuthExchangeException.class)
    public ResponseEntity<ApiError> handleOAuthExchange(OAuthExchangeException ex, HttpServletRequest req) {
        String requestId = MDC.get("requestId");
        if (requestId == null) requestId = "";
        ApiError err = new ApiError(Instant.now(), HttpStatus.BAD_GATEWAY.value(), "Bad Gateway", ex.getMessage(), req.getRequestURI(), requestId);
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(err);
    }

    @ExceptionHandler(TokenNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(TokenNotFoundException ex, HttpServletRequest req) {
        String requestId = MDC.get("requestId");
        if (requestId == null) requestId = "";
        ApiError err = new ApiError(Instant.now(), HttpStatus.NOT_FOUND.value(), "Not Found", ex.getMessage(), req.getRequestURI(), requestId);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiError> handleRuntime(RuntimeException ex, HttpServletRequest req) {
        String requestId = MDC.get("requestId");
        if (requestId == null) requestId = "";
        ApiError err = new ApiError(Instant.now(), HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error", ex.getMessage(), req.getRequestURI(), requestId);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
    }
}
