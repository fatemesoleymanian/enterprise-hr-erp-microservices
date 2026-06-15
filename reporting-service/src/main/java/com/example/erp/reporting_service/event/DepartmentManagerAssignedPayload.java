package com.example.erp.reporting_service.event;

import java.time.OffsetDateTime;
import java.util.UUID;

public record DepartmentManagerAssignedPayload(
        UUID departmentId,
        UUID managerUserId,
        OffsetDateTime assignedAt
) {
}
