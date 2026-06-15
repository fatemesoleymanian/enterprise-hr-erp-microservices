package com.example.erp.reporting_service.web.dto;

import java.util.List;
import java.util.UUID;

public record DepartmentAttendanceReport(
        UUID departmentId,
        int year,
        int month,
        List<MonthlyAttendanceReportRow> employees,
        AttendanceTotals totals
) {
}
