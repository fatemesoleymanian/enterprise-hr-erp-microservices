package com.example.identity_service.user;

public class InvalidRoleException extends RuntimeException {

    public InvalidRoleException(String role) {
        super("Invalid role: " + role);
    }
}
