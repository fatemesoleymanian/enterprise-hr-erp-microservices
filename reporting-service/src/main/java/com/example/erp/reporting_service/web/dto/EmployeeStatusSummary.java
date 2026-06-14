package com.example.erp.reporting_service.web.dto;

import java.util.Map;
import java.util.UUID;

public record EmployeeStatusSummary(
        UUID departmentId,
        long totalEmployees,
        long activeEmployees,
        long inactiveEmployees,
        Map<String, Long> statusCounts
) {
}
