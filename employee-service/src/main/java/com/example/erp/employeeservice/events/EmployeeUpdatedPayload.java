package com.example.erp.employeeservice.events;


import com.example.erp.employeeservice.domain.Status;

import java.time.OffsetDateTime;
import java.util.UUID;

public record EmployeeUpdatedPayload(
        UUID id,
        UUID userId,
        String employeeNumber,
        String firstName,
        String lastName,
        String email,
        UUID departmentId,
        UUID managerEmployeeId,
        OffsetDateTime hireDate,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        Integer version,
        Status status
) {}