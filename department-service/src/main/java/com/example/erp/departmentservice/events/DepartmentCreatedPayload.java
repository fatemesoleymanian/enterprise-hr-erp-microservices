package com.example.erp.departmentservice.events;


import java.time.OffsetDateTime;
import java.util.UUID;

public record DepartmentCreatedPayload(
        UUID departmentId,
        String name,
        String description,
        UUID parentDepartmentId,
        UUID managerUserId,
        OffsetDateTime createdAt
) {}
