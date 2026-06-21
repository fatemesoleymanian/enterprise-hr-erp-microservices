package com.example.erp.employeeservice.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public class UpdateDepartmentEmployeeResponseDto
{
    private UUID id;
    private UUID departmentId;
    private OffsetDateTime updatedAt;

    public UpdateDepartmentEmployeeResponseDto() {
    }

    public UpdateDepartmentEmployeeResponseDto(UUID id, UUID departmentId, OffsetDateTime updatedAt) {
        this.id = id;
        this.departmentId = departmentId;
        this.updatedAt = updatedAt;
    }


    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(UUID departmentId) {
        this.departmentId = departmentId;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
