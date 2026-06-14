package com.example.erp.reporting_service.event;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public record AttendanceViolationDetectedPayload(
        UUID attendanceRecordId,
        UUID employeeId,
        LocalDate attendanceDate,
        String violationType,
        OffsetDateTime detectedAt
) {
}
