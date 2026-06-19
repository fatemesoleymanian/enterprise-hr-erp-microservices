package com.example.erp.employeeservice.exceptions;

import java.util.UUID;

public class managerUserNotFoundCustomException extends RuntimeException
{
    public managerUserNotFoundCustomException(UUID managerUserId) {
        super("manager with Userid " + managerUserId + " not found");
    }
}
