package com.example.erp.employeeservice.exceptions;

import java.util.UUID;

public class EmployeeNotFoundCustomException extends RuntimeException
{
    public EmployeeNotFoundCustomException(UUID id) {
        super("Employee with id " + id + " not found");
    }
}
