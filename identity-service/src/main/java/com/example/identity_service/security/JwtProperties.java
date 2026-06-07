package com.example.identity_service.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.jwt")
public record JwtProperties(
        String secret,
        long expirationMinutes
) {
}
