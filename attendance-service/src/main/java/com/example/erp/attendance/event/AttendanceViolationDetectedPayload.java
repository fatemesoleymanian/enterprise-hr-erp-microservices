package com.example.erp.attendance.event;

import com.example.erp.attendance.domain.AttendanceStatus;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public record AttendanceViolationDetectedPayload(
        UUID attendanceRecordId,
        UUID employeeId,
        LocalDate attendanceDate,
        AttendanceStatus violationType,
        OffsetDateTime detectedAt
) {
}
