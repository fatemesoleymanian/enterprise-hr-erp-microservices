package com.example.erp.departmentservice.exceptions;

import java.util.UUID;

public class UserNotFoundCustomException extends RuntimeException
{
    public UserNotFoundCustomException(UUID userId) {
        super("User with id :" + userId + "does not exist!");
    }
}
