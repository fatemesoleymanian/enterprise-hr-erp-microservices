package com.example.identity_service.user.event;

import java.util.UUID;

public record UserDisabledEvent(
        UUID userId,
        String email
) {
}
