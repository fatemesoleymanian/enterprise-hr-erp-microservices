package com.example.erp.reporting_service.web.dto;

public record AttendanceTotals(
        int presentDays,
        int lateDays,
        int earlyLeaveDays,
        int absentDays,
        int totalWorkedMinutes
) {
}
