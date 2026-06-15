package com.example.erp.reporting_service.event;

import java.time.OffsetDateTime;
import java.util.UUID;

public record EmployeeStatusChangedPayload(
        UUID employeeId,
        String status,
        OffsetDateTime changedAt
) {
}
