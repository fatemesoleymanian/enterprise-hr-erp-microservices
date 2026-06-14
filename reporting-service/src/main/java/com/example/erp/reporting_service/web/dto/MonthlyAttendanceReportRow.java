package com.example.erp.reporting_service.web.dto;

import java.util.UUID;

public record MonthlyAttendanceReportRow(
        UUID employeeId,
        UUID departmentId,
        int year,
        int month,
        int presentDays,
        int lateDays,
        int earlyLeaveDays,
        int absentDays,
        int totalWorkedMinutes
) {
}
