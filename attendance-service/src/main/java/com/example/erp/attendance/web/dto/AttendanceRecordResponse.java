package com.example.erp.attendance.web.dto;

import com.example.erp.attendance.domain.AttendanceRecord;
import com.example.erp.attendance.domain.AttendanceStatus;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record AttendanceRecordResponse(
        UUID id,
        UUID employeeId,
        LocalDate attendanceDate,
        OffsetDateTime checkInAt,
        OffsetDateTime checkOutAt,
        List<AttendanceStatus> statuses,
        int workedMinutes
) {
    public static AttendanceRecordResponse from(AttendanceRecord record) {
        return new AttendanceRecordResponse(
                record.getId(),
                record.getEmployeeId(),
                record.getAttendanceDate(),
                record.getCheckInAt(),
                record.getCheckOutAt(),
                statusesFrom(record),
                record.getWorkedMinutes()
        );
    }

    public static List<AttendanceStatus> statusesFrom(AttendanceRecord record) {
        return List.of(record.getStatus());
    }
}
