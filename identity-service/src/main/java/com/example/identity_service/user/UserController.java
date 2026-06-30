package com.example.identity_service.user;

import com.example.identity_service.user.dto.CreateUserRequest;
import com.example.identity_service.user.dto.UpdateUserRolesRequest;
import com.example.identity_service.user.dto.UpdateUserStatusRequest;
import com.example.identity_service.user.dto.UserResponse;
import jakarta.validation.Valid;
import com.example.erp.common.api.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.UUID;

@Validated
@RestController
@RequestMapping("/api/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private final UserManagementService userManagementService;

    public UserController(UserManagementService userManagementService) {
        this.userManagementService = userManagementService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@Valid @RequestBody CreateUserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(userManagementService.createUser(request)));
    }

    @GetMapping("/{id}")
    public ApiResponse<UserResponse> getUser(@PathVariable UUID id) {
        return ApiResponse.success(userManagementService.getUser(id));
    }

    @PatchMapping("/{id}/status")
    public ApiResponse<UserResponse> updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateUserStatusRequest request
    ) {
        return ApiResponse.success(userManagementService.updateStatus(id, request));
    }

    @PutMapping("/{id}/roles")
    public ApiResponse<UserResponse> updateRoles(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateUserRolesRequest request
    ) {
        return ApiResponse.success(userManagementService.updateRoles(id, request));
    }
}
