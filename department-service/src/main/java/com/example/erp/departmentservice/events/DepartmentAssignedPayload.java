package com.example.erp.departmentservice.events;

import java.time.OffsetDateTime;
import java.util.UUID;

public record DepartmentAssignedPayload(
        UUID id, UUID managerUserId, OffsetDateTime updatedAt){}
