package com.bm_nttdata.account_ms.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleAccountNotFoundException(AccountNotFoundException ex) {
        return createErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), "ACCOUNT_NOT_FOUND");
    }

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<Map<String, Object>> handleBusinessRuleException(BusinessRuleException ex) {
        return createErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(), "BUSINESS_RULE_VIOLATION");
    }

    private ResponseEntity<Map<String, Object>> createErrorResponse(
            HttpStatus status, String message, String code) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", message);
        body.put("code", code);
        return new ResponseEntity<>(body, status);
    }
}
