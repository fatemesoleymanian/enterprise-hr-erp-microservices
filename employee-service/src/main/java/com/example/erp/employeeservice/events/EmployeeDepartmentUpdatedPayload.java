package com.example.erp.employeeservice.events;

import java.time.OffsetDateTime;
import java.util.UUID;

public record EmployeeDepartmentUpdatedPayload
        (UUID id, UUID department, OffsetDateTime updatedAt) {}
