package com.example.erp.reporting_service.event;

import java.time.OffsetDateTime;
import java.util.UUID;

public record EmployeeDepartmentChangedPayload(
        UUID employeeId,
        UUID departmentId,
        OffsetDateTime changedAt
) {
}
