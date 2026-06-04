package com.example.erp.attendance.web.dto;

import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;
import java.util.UUID;

public record CheckInRequest(
        @NotNull UUID employeeId,
        @NotNull OffsetDateTime checkInAt
) {
}
