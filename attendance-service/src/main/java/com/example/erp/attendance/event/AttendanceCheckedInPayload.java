package com.example.erp.attendance.event;

import com.example.erp.attendance.domain.AttendanceStatus;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record AttendanceCheckedInPayload(
        UUID attendanceRecordId,
        UUID employeeId,
        LocalDate attendanceDate,
        OffsetDateTime checkInAt,
        List<AttendanceStatus> statuses
) {
}
