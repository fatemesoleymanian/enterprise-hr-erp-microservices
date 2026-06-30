package com.example.identity_service.user.dto;

import com.example.identity_service.user.UserStatus;

import java.util.List;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String email,
        String fullName,
        UserStatus status,
        List<String> roles
) {
}
