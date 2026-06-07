package com.example.identity_service.auth;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class DisabledUserException extends RuntimeException {

    public DisabledUserException() {
        super("User account is disabled.");
    }
}
