package com.example.erp.employeeservice.exceptions;

import java.util.UUID;

public class userNotFoundCustomException extends RuntimeException
{
    public userNotFoundCustomException(UUID user_id) {
        super("User with id " + user_id + " not found");
    }
}
