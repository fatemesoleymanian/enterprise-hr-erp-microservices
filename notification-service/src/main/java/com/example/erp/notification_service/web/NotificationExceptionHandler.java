package com.example.erp.notification_service.web;

import com.example.erp.common.api.ErrorResponse;
import com.example.erp.notification_service.service.NotificationNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class NotificationExceptionHandler {

    @ExceptionHandler(NotificationNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
            NotificationNotFoundException exception,
            HttpServletRequest request
    ) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.of("NOTIFICATION_NOT_FOUND", "Notification not found.", request.getRequestURI()));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleInvalidFilter(Exception exception, HttpServletRequest request) {
        return ResponseEntity.badRequest()
                .body(ErrorResponse.of("INVALID_NOTIFICATION_FILTER", "Invalid notification filter.", request.getRequestURI()));
    }
}
