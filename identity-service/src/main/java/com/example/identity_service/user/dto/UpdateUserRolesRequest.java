package com.example.identity_service.user.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotBlank;

import java.util.Set;

public record UpdateUserRolesRequest(
        @NotEmpty
        Set<@NotBlank String> roles
) {
}
