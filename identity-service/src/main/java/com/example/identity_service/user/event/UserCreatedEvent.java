package com.example.identity_service.user.event;

import java.util.List;
import java.util.UUID;

public record UserCreatedEvent(
        UUID userId,
        String email,
        List<String> roles
) {
}
