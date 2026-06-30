package com.example.identity_service.security;

import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class JwtPropertiesTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(TestConfig.class)
            .withPropertyValues(
                    "spring.jwt.secret=test-secret",
                    "spring.jwt.expiration-minutes=60"
            );

    @Test
    void bindsJwtPropertiesFromConfiguration() {
        contextRunner.run(context -> {
            var properties = context.getBean(JwtProperties.class);

            assertThat(properties.secret()).isEqualTo("test-secret");
            assertThat(properties.expirationMinutes()).isEqualTo(60);
        });
    }

    @EnableConfigurationProperties(JwtProperties.class)
    static class TestConfig {
    }
}
