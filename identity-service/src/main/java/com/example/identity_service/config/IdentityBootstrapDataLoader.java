package com.example.identity_service.config;

import com.example.identity_service.role.Role;
import com.example.identity_service.role.RoleRepository;
import com.example.identity_service.user.User;
import com.example.identity_service.user.UserRepository;
import com.example.identity_service.user.UserStatus;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;

@Component
public class IdentityBootstrapDataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final IdentityBootstrapProperties properties;

    public IdentityBootstrapDataLoader(UserRepository userRepository,
                                       RoleRepository roleRepository,
                                       PasswordEncoder passwordEncoder,
                                       IdentityBootstrapProperties properties) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.properties = properties;
    }

    @Override
    public void run(String... args) {
        if (properties.email() == null || properties.password() == null || properties.fullName() == null) {
            return;
        }

        if (userRepository.findByEmail(properties.email()).isPresent()) {
            return;
        }

        Role adminRole = roleRepository.findByName("ADMIN").orElseGet(() -> {
            Role role = new Role();
            role.setName("ADMIN");
            return roleRepository.save(role);
        });

        User admin = new User();
        admin.setEmail(properties.email());
        admin.setPasswordHash(passwordEncoder.encode(properties.password()));
        admin.setFullName(properties.fullName());
        admin.setStatus(UserStatus.ACTIVE);
        admin.setRoles(new LinkedHashSet<>(java.util.Set.of(adminRole)));

        userRepository.save(admin);
    }
}
