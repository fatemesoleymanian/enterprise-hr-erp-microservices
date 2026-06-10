package com.example.identity_service.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

class SecurityConfigTest {

    private final SecurityConfig securityConfig = new SecurityConfig();

    @Test
    void exposesBcryptPasswordEncoderJwtDecoderAndJwtAuthenticationConverter() {
        PasswordEncoder passwordEncoder = securityConfig.passwordEncoder();

        assertThat(passwordEncoder).isInstanceOf(BCryptPasswordEncoder.class);
        assertThat(securityConfig.jwtDecoder(new JwtProperties("test-secret-key-test-secret-key", 60))).isNotNull();
        assertThat(securityConfig.jwtAuthenticationConverter()).isNotNull();
    }
}
