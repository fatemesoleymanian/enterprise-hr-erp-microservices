package com.example.identity_service.security;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.assertj.core.api.Assertions.assertThat;

class SecurityConfigTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(SecurityConfig.class)
            .withPropertyValues(
                    "spring.jwt.secret=test-secret-key-test-secret-key",
                    "spring.jwt.expiration-minutes=60"
            );

    @Test
    void exposesBcryptPasswordEncoderAndSecurityFilterChain() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(PasswordEncoder.class);
            assertThat(context.getBean(PasswordEncoder.class)).isInstanceOf(BCryptPasswordEncoder.class);
            assertThat(context).hasSingleBean(SecurityFilterChain.class);
        });
    }
}
