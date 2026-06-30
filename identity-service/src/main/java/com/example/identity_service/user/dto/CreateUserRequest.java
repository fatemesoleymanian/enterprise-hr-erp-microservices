package com.example.identity_service.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.Set;

public record CreateUserRequest(
        @NotBlank
        String email,
        @NotBlank
        String password,
        @NotBlank
        String fullName,
        @NotEmpty
        Set<@NotBlank String> roles
) {
}
