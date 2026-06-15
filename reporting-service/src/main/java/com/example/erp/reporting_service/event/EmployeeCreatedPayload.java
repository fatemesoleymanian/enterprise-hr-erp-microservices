package com.example.erp.reporting_service.event;

import java.time.OffsetDateTime;
import java.util.UUID;

public record EmployeeCreatedPayload(
        UUID employeeId,
        UUID departmentId,
        String status,
        String jobTitle,
        OffsetDateTime createdAt
) {
}
