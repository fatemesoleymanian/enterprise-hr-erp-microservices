package com.example.erp.employeeservice.exceptions;

import java.util.UUID;

public class ManagerUserNotFoundCustomException extends RuntimeException
{
    public ManagerUserNotFoundCustomException(UUID managerUserId) {
        super("Manager with Userid " + managerUserId + " not found");
    }
}
