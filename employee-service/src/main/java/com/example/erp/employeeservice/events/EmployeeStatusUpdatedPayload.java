package com.example.erp.employeeservice.events;

import com.example.erp.employeeservice.domain.Status;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.UUID;

public record EmployeeStatusUpdatedPayload(UUID id, Status status, OffsetDateTime updatedAt)
{}
