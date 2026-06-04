package com.example.erp.attendance.web.dto;

import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;
import java.util.UUID;

public record CheckOutRequest(
        @NotNull UUID employeeId,
        @NotNull OffsetDateTime checkOutAt
) {
}
