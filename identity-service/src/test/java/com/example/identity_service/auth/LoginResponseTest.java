package com.example.identity_service.auth;

import com.example.identity_service.auth.dto.LoginResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LoginResponseTest {

    @Test
    void storesTokenTokenTypeAndExpiration() {
        var response = new LoginResponse("jwt-token", "Bearer", 60L);

        assertEquals("jwt-token", response.accessToken());
        assertEquals("Bearer", response.tokenType());
        assertEquals(60L, response.expiresInMinutes());
    }
}
