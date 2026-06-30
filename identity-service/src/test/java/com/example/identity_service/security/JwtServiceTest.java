package com.example.identity_service.security;

import com.example.identity_service.role.Role;
import com.example.identity_service.user.User;
import com.example.identity_service.user.UserStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void generatedTokenContainsSubjectAndRoles() throws Exception {
        var properties = new JwtProperties("test-secret-key-test-secret-key", 60);
        var jwtService = new JwtService(properties, objectMapper);
        var user = user("admin@example.com", "ADMIN", "HR_MANAGER");

        String token = jwtService.generateToken(user);
        Map<String, Object> claims = decodePayload(token);

        assertThat(claims.get("sub")).isEqualTo("admin@example.com");
        assertThat((List<String>) claims.get("roles")).containsExactlyInAnyOrder("ADMIN", "HR_MANAGER");
        assertThat(claims.get("iat")).isNotNull();
        assertThat(claims.get("exp")).isNotNull();
    }

    private Map<String, Object> decodePayload(String token) throws Exception {
        String[] parts = token.split("\\.");
        assertThat(parts).hasSize(3);
        String json = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
        return objectMapper.readValue(json, LinkedHashMap.class);
    }

    private static User user(String email, String... roles) {
        var user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail(email);
        user.setPasswordHash("hashed-password");
        user.setFullName("Admin User");
        user.setStatus(UserStatus.ACTIVE);
        user.setRoles(java.util.Arrays.stream(roles).map(JwtServiceTest::role).collect(java.util.stream.Collectors.toSet()));
        return user;
    }

    private static Role role(String roleName) {
        var role = new Role();
        role.setId(UUID.randomUUID());
        role.setName(roleName);
        return role;
    }
}
