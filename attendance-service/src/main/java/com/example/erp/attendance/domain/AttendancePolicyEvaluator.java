package com.example.erp.attendance.domain;

import org.springframework.stereotype.Component;

import java.time.LocalTime;

@Component
public class AttendancePolicyEvaluator {

    private static final LocalTime DEFAULT_LATE_AFTER = LocalTime.of(9, 15);
    private static final LocalTime DEFAULT_EARLY_LEAVE_BEFORE = LocalTime.of(16, 0);

    public AttendanceStatus evaluateCheckIn(LocalTime checkInTime) {
        if (checkInTime.isAfter(DEFAULT_LATE_AFTER)) {
            return AttendanceStatus.LATE;
        }

        return AttendanceStatus.PRESENT;
    }

    public AttendanceStatus evaluateCheckOut(LocalTime checkOutTime) {
        if (checkOutTime.isBefore(DEFAULT_EARLY_LEAVE_BEFORE)) {
            return AttendanceStatus.EARLY_LEAVE;
        }

        return AttendanceStatus.PRESENT;
    }
}
