package com.example.identity_service.config;

import com.example.identity_service.role.Role;
import com.example.identity_service.role.RoleRepository;
import com.example.identity_service.user.User;
import com.example.identity_service.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IdentityBootstrapDataLoaderTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void seedsDefaultAdminWhenMissing() {
        var properties = new IdentityBootstrapProperties("admin@example.com", "Admin@12345", "System Admin");
        var dataLoader = new IdentityBootstrapDataLoader(userRepository, roleRepository, passwordEncoder, properties);
        when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.empty());
        when(roleRepository.findByName("ADMIN")).thenReturn(Optional.empty());
        when(roleRepository.save(org.mockito.ArgumentMatchers.any(Role.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(passwordEncoder.encode("Admin@12345")).thenReturn("hashed-password");

        dataLoader.run();

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertThat(userCaptor.getValue().getEmail()).isEqualTo("admin@example.com");
        assertThat(userCaptor.getValue().getPasswordHash()).isEqualTo("hashed-password");
        verify(roleRepository).save(org.mockito.ArgumentMatchers.any(Role.class));
    }

    @Test
    void doesNotSeedAdminWhenUserAlreadyExists() {
        var properties = new IdentityBootstrapProperties("admin@example.com", "Admin@12345", "System Admin");
        var dataLoader = new IdentityBootstrapDataLoader(userRepository, roleRepository, passwordEncoder, properties);
        when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(new User()));

        dataLoader.run();

        verify(userRepository, never()).save(org.mockito.ArgumentMatchers.any());
        verify(roleRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }
}
