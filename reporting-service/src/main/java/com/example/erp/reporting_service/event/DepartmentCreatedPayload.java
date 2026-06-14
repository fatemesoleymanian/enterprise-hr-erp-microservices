package com.example.erp.reporting_service.event;

import java.time.OffsetDateTime;
import java.util.UUID;

public record DepartmentCreatedPayload(
        UUID departmentId,
        String name,
        UUID managerUserId,
        OffsetDateTime createdAt
) {
}
