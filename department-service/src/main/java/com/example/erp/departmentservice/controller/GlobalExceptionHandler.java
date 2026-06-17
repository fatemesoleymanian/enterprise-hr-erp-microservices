package com.example.erp.departmentservice.controller;

import com.example.erp.departmentservice.exceptions.DepartmentFindByIdNullCustomException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DepartmentFindByIdNullCustomException.class)
    public ResponseEntity<?> handleNotFound(DepartmentFindByIdNullCustomException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}

