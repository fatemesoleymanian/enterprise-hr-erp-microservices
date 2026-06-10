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
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class UserManagementService {

    private static final Logger log = LoggerFactory.getLogger(UserManagementService.class);

    private static final Set<String> ALLOWED_ROLES = Set.of(
            "ADMIN",
            "HR_MANAGER",
            "DEPARTMENT_MANAGER",
            "EMPLOYEE"
    );

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;

    public UserManagementService(UserRepository userRepository,
                                 RoleRepository roleRepository,
                                 PasswordEncoder passwordEncoder,
                                 ApplicationEventPublisher eventPublisher) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new DuplicateEmailException("Email already exists.");
        }

        User user = new User();
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setFullName(request.fullName());
        user.setStatus(UserStatus.ACTIVE);
        user.setRoles(resolveRoles(request.roles()));

        User saved = userRepository.save(user);
        publishEvent(new UserCreatedEvent(saved.getId(), saved.getEmail(), rolesOf(saved)));
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public UserResponse getUser(UUID userId) {
        return toResponse(findUser(userId));
    }

    @Transactional
    public UserResponse updateStatus(UUID userId, UpdateUserStatusRequest request) {
        User user = findUser(userId);
        UserStatus previousStatus = user.getStatus();
        user.setStatus(request.status());

        User saved = userRepository.save(user);
        if (previousStatus != UserStatus.DISABLED && request.status() == UserStatus.DISABLED) {
            publishEvent(new UserDisabledEvent(saved.getId(), saved.getEmail()));
        }
        return toResponse(saved);
    }

    @Transactional
    public UserResponse updateRoles(UUID userId, UpdateUserRolesRequest request) {
        User user = findUser(userId);
        user.setRoles(resolveRoles(request.roles()));

        User saved = userRepository.save(user);
        publishEvent(new UserRolesChangedEvent(saved.getId(), saved.getEmail(), rolesOf(saved)));
        return toResponse(saved);
    }

    private void publishEvent(Object event) {
        log.info("Publishing domain event: {}", event.getClass().getSimpleName());
        eventPublisher.publishEvent(event);
    }

    private User findUser(UUID userId) {
        return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
    }

    private Set<Role> resolveRoles(Set<String> roleNames) {
        Set<Role> roles = new LinkedHashSet<>();
        for (String roleName : roleNames) {
            if (!ALLOWED_ROLES.contains(roleName)) {
                throw new InvalidRoleException(roleName);
            }
            roles.add(roleRepository.findByName(roleName).orElseGet(() -> {
                Role role = new Role();
                role.setName(roleName);
                return roleRepository.save(role);
            }));
        }
        return roles;
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getStatus(),
                rolesOf(user)
        );
    }

    private List<String> rolesOf(User user) {
        return user.getRoles().stream()
                .map(Role::getName)
                .sorted(Comparator.naturalOrder())
                .toList();
    }
}
