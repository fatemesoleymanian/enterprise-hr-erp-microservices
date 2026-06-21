package com.example.erp.employeeservice.exceptions;

import java.util.UUID;

public class DepartmentNotFoundCustomException extends RuntimeException{
    public DepartmentNotFoundCustomException(UUID id) {
        super("Department with id " + id + " not found");
    }
}
