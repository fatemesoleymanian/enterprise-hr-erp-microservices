package com.example.identity_service.user.dto;

import com.example.identity_service.user.UserStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateUserStatusRequest(
        @NotNull
        UserStatus status
) {
}
