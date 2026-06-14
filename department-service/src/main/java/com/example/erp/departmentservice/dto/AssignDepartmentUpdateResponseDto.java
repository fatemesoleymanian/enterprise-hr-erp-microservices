package com.example.erp.departmentservice.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public class AssignDepartmentUpdateResponseDto
{

    private UUID id;
    private UUID managerId;
    private OffsetDateTime updatedAt;

    public AssignDepartmentUpdateResponseDto() {
    }

    public AssignDepartmentUpdateResponseDto(UUID id, UUID managerId, OffsetDateTime updatedAt) {
        this.id = id;
        this.managerId = managerId;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getManagerId() {
        return managerId;
    }

    public void setManagerId(UUID managerId) {
        this.managerId = managerId;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
