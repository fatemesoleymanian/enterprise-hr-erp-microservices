package com.example.erp.attendance.web.dto;

import java.util.UUID;

public record MonthlyAttendanceSummaryResponse(
        UUID employeeId,
        int year,
        int month,
        int presentDays,
        int lateDays,
        int earlyLeaveDays,
        int absentDays,
        int totalWorkedMinutes
) {
}
