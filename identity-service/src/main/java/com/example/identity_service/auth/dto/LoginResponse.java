package com.example.identity_service.auth.dto;

public record LoginResponse(
        String accessToken,
        String tokenType,
        long expiresInMinutes
) {
}
