package com.example.erp.employeeservice.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public class UpdateStatusEmployeeResponseDto
{
    private UUID id;
    private String status;
    private OffsetDateTime updatedAt;

    public UpdateStatusEmployeeResponseDto() {
    }

    public UpdateStatusEmployeeResponseDto(UUID id, String status, OffsetDateTime updatedAt) {
        this.id = id;
        this.status = status;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
