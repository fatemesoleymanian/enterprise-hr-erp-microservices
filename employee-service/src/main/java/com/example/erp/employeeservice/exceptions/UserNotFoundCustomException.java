package com.example.erp.employeeservice.exceptions;

import java.util.UUID;

public class UserNotFoundCustomException extends RuntimeException
{
    public UserNotFoundCustomException(UUID user_id) {
        super("User with id " + user_id + " not found");
    }
}
