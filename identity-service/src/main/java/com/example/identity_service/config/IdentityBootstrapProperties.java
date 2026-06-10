package com.example.identity_service.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "identity.bootstrap.admin")
public record IdentityBootstrapProperties(
        String email,
        String password,
        String fullName
) {
}
