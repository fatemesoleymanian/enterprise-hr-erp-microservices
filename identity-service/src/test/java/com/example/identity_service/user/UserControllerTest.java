package com.example.identity_service.user;

import com.example.identity_service.security.SecurityConfig;
import com.example.identity_service.security.JwtProperties;
import com.example.identity_service.security.JwtService;
import com.example.identity_service.role.Role;
import com.example.identity_service.user.dto.CreateUserRequest;
import com.example.identity_service.user.dto.UpdateUserRolesRequest;
import com.example.identity_service.user.dto.UpdateUserStatusRequest;
import com.example.identity_service.user.dto.UserResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import({SecurityConfig.class, UserExceptionHandler.class})
@TestPropertySource(properties = {
        "spring.jwt.secret=test-secret-key-test-secret-key",
        "spring.jwt.expiration-minutes=60"
})
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserManagementService userManagementService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCanCreateUser() throws Exception {
        var userId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        when(userManagementService.createUser(org.mockito.ArgumentMatchers.any(CreateUserRequest.class)))
                .thenReturn(new UserResponse(userId, "admin@example.com", "Admin User", UserStatus.ACTIVE, List.of("ADMIN")));

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "admin@example.com",
                                  "password": "secret123",
                                  "fullName": "Admin User",
                                  "roles": ["ADMIN"]
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(userId.toString()))
                .andExpect(jsonPath("$.data.email").value("admin@example.com"))
                .andExpect(jsonPath("$.data.roles[0]").value("ADMIN"));
    }

    @Test
    void adminBearerTokenCanCreateUser() throws Exception {
        var userId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        when(userManagementService.createUser(org.mockito.ArgumentMatchers.any(CreateUserRequest.class)))
                .thenReturn(new UserResponse(userId, "admin@example.com", "Admin User", UserStatus.ACTIVE, List.of("ADMIN")));

        String token = adminToken();

        mockMvc.perform(post("/api/users")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "admin@example.com",
                                  "password": "secret123",
                                  "fullName": "Admin User",
                                  "roles": ["ADMIN"]
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.email").value("admin@example.com"));
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void employeeCannotCreateUser() throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "admin@example.com",
                                  "password": "secret123",
                                  "fullName": "Admin User",
                                  "roles": ["ADMIN"]
                                }
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void duplicateEmailReturnsConflict() throws Exception {
        doThrow(new DuplicateEmailException("Email already exists."))
                .when(userManagementService)
                .createUser(org.mockito.ArgumentMatchers.any(CreateUserRequest.class));

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "admin@example.com",
                                  "password": "secret123",
                                  "fullName": "Admin User",
                                  "roles": ["ADMIN"]
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorCode").value("DUPLICATE_EMAIL"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void missingRequiredFieldsReturnBadRequest() throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("INVALID_REQUEST"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCanReadAndUpdateUser() throws Exception {
        var userId = UUID.fromString("22222222-2222-2222-2222-222222222222");
        when(userManagementService.getUser(userId))
                .thenReturn(new UserResponse(userId, "admin@example.com", "Admin User", UserStatus.ACTIVE, List.of("ADMIN")));
        when(userManagementService.updateStatus(userId, new UpdateUserStatusRequest(UserStatus.DISABLED)))
                .thenReturn(new UserResponse(userId, "admin@example.com", "Admin User", UserStatus.DISABLED, List.of("ADMIN")));
        when(userManagementService.updateRoles(userId, new UpdateUserRolesRequest(Set.of("HR_MANAGER"))))
                .thenReturn(new UserResponse(userId, "admin@example.com", "Admin User", UserStatus.ACTIVE, List.of("HR_MANAGER")));

        mockMvc.perform(get("/api/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("ACTIVE"));

        mockMvc.perform(patch("/api/users/{id}/status", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"status":"DISABLED"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("DISABLED"));

        mockMvc.perform(put("/api/users/{id}/roles", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"roles":["HR_MANAGER"]}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.roles[0]").value("HR_MANAGER"));
    }

    private String adminToken() {
        var jwtService = new JwtService(
                new JwtProperties("test-secret-key-test-secret-key", 60),
                new ObjectMapper()
        );
        var admin = new User();
        admin.setEmail("admin@example.com");
        admin.setPasswordHash("hashed-password");
        admin.setFullName("Admin User");
        admin.setStatus(UserStatus.ACTIVE);
        var role = new Role();
        role.setName("ADMIN");
        admin.setRoles(Set.of(role));
        return jwtService.generateToken(admin);
    }
}
