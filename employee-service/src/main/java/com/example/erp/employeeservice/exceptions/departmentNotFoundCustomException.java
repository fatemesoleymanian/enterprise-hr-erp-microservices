package com.example.erp.employeeservice.exceptions;

import java.util.UUID;

public class departmentNotFoundCustomException extends RuntimeException{
    public departmentNotFoundCustomException(UUID id) {
        super("Department with id " + id + " not found");
    }
}
