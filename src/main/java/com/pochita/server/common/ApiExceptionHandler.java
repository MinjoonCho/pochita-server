package com.pochita.server.common;

import java.time.Instant;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Map<String, Object>> handleApiException(ApiException exception) {
        return ResponseEntity.status(exception.getStatus()).body(Map.of(
                "timestamp", Instant.now().toString(),
                "status", exception.getStatus().value(),
                "error", exception.getStatus().getReasonPhrase(),
                "message", exception.getMessage()
        ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("요청값이 올바르지 않습니다.");

        return ResponseEntity.badRequest().body(Map.of(
                "timestamp", Instant.now().toString(),
                "status", 400,
                "error", "Bad Request",
                "message", message
        ));
    }
}
