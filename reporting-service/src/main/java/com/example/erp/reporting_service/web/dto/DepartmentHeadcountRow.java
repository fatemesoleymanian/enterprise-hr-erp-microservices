package com.example.erp.reporting_service.web.dto;

import java.util.UUID;

public record DepartmentHeadcountRow(
        UUID departmentId,
        String departmentName,
        UUID managerUserId,
        long activeEmployees
) {
}
