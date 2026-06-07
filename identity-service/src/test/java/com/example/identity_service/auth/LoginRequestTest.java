package com.example.identity_service.auth;

import com.example.identity_service.auth.dto.LoginRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LoginRequestTest {

    @Test
    void storesEmailAndPassword() {
        var request = new LoginRequest("admin@example.com", "secret123");

        assertEquals("admin@example.com", request.email());
        assertEquals("secret123", request.password());
    }
}
