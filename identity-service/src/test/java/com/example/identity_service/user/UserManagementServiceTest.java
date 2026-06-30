package com.example.identity_service.user;

import com.example.identity_service.role.Role;
import com.example.identity_service.role.RoleRepository;
import com.example.identity_service.user.dto.CreateUserRequest;
import com.example.identity_service.user.dto.UpdateUserRolesRequest;
import com.example.identity_service.user.dto.UpdateUserStatusRequest;
import com.example.identity_service.user.dto.UserResponse;
import com.example.identity_service.user.event.UserCreatedEvent;
import com.example.identity_service.user.event.UserDisabledEvent;
import com.example.identity_service.user.event.UserRolesChangedEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserManagementServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @InjectMocks
    private UserManagementService userManagementService;

    @Test
    void createUserPersistsUserHashesPasswordAndPublishesEvent() {
        var request = new CreateUserRequest(
                "admin@example.com",
                "secret123",
                "Admin User",
                Set.of("ADMIN", "HR_MANAGER")
        );
        when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("secret123")).thenReturn("hashed-password");
        when(roleRepository.findByName("ADMIN")).thenReturn(Optional.of(role("ADMIN")));
        when(roleRepository.findByName("HR_MANAGER")).thenReturn(Optional.of(role("HR_MANAGER")));
        when(userRepository.save(org.mockito.ArgumentMatchers.any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            ReflectionTestUtils.setField(user, "id", UUID.fromString("11111111-1111-1111-1111-111111111111"));
            return user;
        });

        var response = userManagementService.createUser(request);

        assertThat(response.id()).isEqualTo(UUID.fromString("11111111-1111-1111-1111-111111111111"));
        assertThat(response.email()).isEqualTo("admin@example.com");
        assertThat(response.fullName()).isEqualTo("Admin User");
        assertThat(response.status()).isEqualTo(UserStatus.ACTIVE);
        assertThat(response.roles()).containsExactly("ADMIN", "HR_MANAGER");

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertThat(userCaptor.getValue().getPasswordHash()).isEqualTo("hashed-password");
        verify(applicationEventPublisher).publishEvent(org.mockito.ArgumentMatchers.any(UserCreatedEvent.class));
    }

    @Test
    void createUserRejectsDuplicateEmail() {
        var existing = user("admin@example.com", UserStatus.ACTIVE, role("ADMIN"));
        when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> userManagementService.createUser(new CreateUserRequest(
                "admin@example.com",
                "secret123",
                "Admin User",
                Set.of("ADMIN")
        ))).isInstanceOf(DuplicateEmailException.class);
    }

    @Test
    void getUserReturnsResponse() {
        var user = user("admin@example.com", UserStatus.ACTIVE, role("ADMIN"), role("HR_MANAGER"));
        ReflectionTestUtils.setField(user, "id", UUID.fromString("22222222-2222-2222-2222-222222222222"));
        when(userRepository.findById(UUID.fromString("22222222-2222-2222-2222-222222222222"))).thenReturn(Optional.of(user));

        var response = userManagementService.getUser(UUID.fromString("22222222-2222-2222-2222-222222222222"));

        assertThat(response.id()).isEqualTo(UUID.fromString("22222222-2222-2222-2222-222222222222"));
        assertThat(response.roles()).containsExactly("ADMIN", "HR_MANAGER");
    }

    @Test
    void updateStatusPublishesDisabledEvent() {
        var user = user("admin@example.com", UserStatus.ACTIVE, role("ADMIN"));
        ReflectionTestUtils.setField(user, "id", UUID.fromString("33333333-3333-3333-3333-333333333333"));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.save(org.mockito.ArgumentMatchers.any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = userManagementService.updateStatus(user.getId(), new UpdateUserStatusRequest(UserStatus.DISABLED));

        assertThat(response.status()).isEqualTo(UserStatus.DISABLED);
        verify(applicationEventPublisher).publishEvent(org.mockito.ArgumentMatchers.any(UserDisabledEvent.class));
    }

    @Test
    void updateRolesPublishesRolesChangedEvent() {
        var user = user("admin@example.com", UserStatus.ACTIVE, role("ADMIN"));
        ReflectionTestUtils.setField(user, "id", UUID.fromString("44444444-4444-4444-4444-444444444444"));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(roleRepository.findByName("HR_MANAGER")).thenReturn(Optional.of(role("HR_MANAGER")));
        when(roleRepository.findByName("DEPARTMENT_MANAGER")).thenReturn(Optional.of(role("DEPARTMENT_MANAGER")));
        when(userRepository.save(org.mockito.ArgumentMatchers.any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = userManagementService.updateRoles(
                user.getId(),
                new UpdateUserRolesRequest(Set.of("HR_MANAGER", "DEPARTMENT_MANAGER"))
        );

        assertThat(response.roles()).containsExactly("DEPARTMENT_MANAGER", "HR_MANAGER");
        verify(applicationEventPublisher).publishEvent(org.mockito.ArgumentMatchers.any(UserRolesChangedEvent.class));
    }

    @Test
    void missingUserThrowsNotFound() {
        when(userRepository.findById(UUID.fromString("55555555-5555-5555-5555-555555555555"))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userManagementService.getUser(UUID.fromString("55555555-5555-5555-5555-555555555555")))
                .isInstanceOf(UserNotFoundException.class);
    }

    private static User user(String email, UserStatus status, Role... roles) {
        var user = new User();
        ReflectionTestUtils.setField(user, "email", email);
        ReflectionTestUtils.setField(user, "passwordHash", "hashed-password");
        ReflectionTestUtils.setField(user, "fullName", "Admin User");
        ReflectionTestUtils.setField(user, "status", status);
        user.setRoles(new LinkedHashSet<>(Set.of(roles)));
        return user;
    }

    private static Role role(String name) {
        var role = new Role();
        ReflectionTestUtils.setField(role, "id", UUID.randomUUID());
        ReflectionTestUtils.setField(role, "name", name);
        return role;
    }
}
