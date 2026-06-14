package com.example.erp.reporting_service.event;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record AttendanceCheckedOutPayload(
        UUID attendanceRecordId,
        UUID employeeId,
        LocalDate attendanceDate,
        OffsetDateTime checkInAt,
        OffsetDateTime checkOutAt,
        List<String> statuses,
        int workedMinutes
) {
}
