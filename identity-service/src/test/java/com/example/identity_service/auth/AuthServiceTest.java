package com.example.identity_service.auth;

import com.example.identity_service.auth.dto.LoginResponse;
import com.example.identity_service.role.Role;
import com.example.identity_service.security.JwtService;
import com.example.identity_service.security.JwtProperties;
import com.example.identity_service.user.User;
import com.example.identity_service.user.UserRepository;
import com.example.identity_service.user.UserStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private JwtProperties jwtProperties;

    @InjectMocks
    private AuthService authService;

    @Test
    void activeUserLoginSucceeds() {
        when(jwtProperties.expirationMinutes()).thenReturn(60L);
        var user = user("admin@example.com", "hashed-password", UserStatus.ACTIVE, "ADMIN");
        when(userRepository.findByEmail("admin@example.com")).thenReturn(java.util.Optional.of(user));
        when(passwordEncoder.matches("secret123", "hashed-password")).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn("jwt-token");

        LoginResponse response = authService.login("admin@example.com", "secret123");

        assertThat(response).isEqualTo(new LoginResponse("jwt-token", "Bearer", 60L));
        verify(jwtService).generateToken(user);
    }

    @Test
    void disabledUserLoginFails() {
        var user = user("disabled@example.com", "hashed-password", UserStatus.DISABLED, "EMPLOYEE");
        when(userRepository.findByEmail("disabled@example.com")).thenReturn(java.util.Optional.of(user));

        assertThatThrownBy(() -> authService.login("disabled@example.com", "secret123"))
                .isInstanceOf(DisabledUserException.class);

        verify(passwordEncoder, never()).matches(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyString());
        verify(jwtService, never()).generateToken(org.mockito.ArgumentMatchers.any());
    }

    private static User user(String email, String passwordHash, UserStatus status, String roleName) {
        var user = new User();
        ReflectionTestUtils.setField(user, "id", UUID.randomUUID());
        ReflectionTestUtils.setField(user, "email", email);
        ReflectionTestUtils.setField(user, "passwordHash", passwordHash);
        ReflectionTestUtils.setField(user, "fullName", "Admin User");
        ReflectionTestUtils.setField(user, "status", status);
        ReflectionTestUtils.setField(user, "roles", Set.of(role(roleName)));
        return user;
    }

    private static Role role(String roleName) {
        var role = new Role();
        ReflectionTestUtils.setField(role, "id", UUID.randomUUID());
        ReflectionTestUtils.setField(role, "name", roleName);
        return role;
    }
}
