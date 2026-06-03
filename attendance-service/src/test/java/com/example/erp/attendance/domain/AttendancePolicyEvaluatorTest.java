package com.example.erp.attendance.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

class AttendancePolicyEvaluatorTest {

    private final AttendancePolicyEvaluator evaluator = new AttendancePolicyEvaluator();

    @Test
    void checkInAtWorkdayStartReturnsPresent() {
        AttendanceStatus status = evaluator.evaluateCheckIn(LocalTime.of(9, 0));

        assertThat(status).isEqualTo(AttendanceStatus.PRESENT);
    }

    @Test
    void checkInAtLateThresholdReturnsPresent() {
        AttendanceStatus status = evaluator.evaluateCheckIn(LocalTime.of(9, 15));

        assertThat(status).isEqualTo(AttendanceStatus.PRESENT);
    }

    @Test
    void checkInAfterLateThresholdReturnsLate() {
        AttendanceStatus status = evaluator.evaluateCheckIn(LocalTime.of(9, 16));

        assertThat(status).isEqualTo(AttendanceStatus.LATE);
    }

    @Test
    void checkOutBeforeEarlyLeaveThresholdReturnsEarlyLeave() {
        AttendanceStatus status = evaluator.evaluateCheckOut(LocalTime.of(15, 59));

        assertThat(status).isEqualTo(AttendanceStatus.EARLY_LEAVE);
    }

    @Test
    void checkOutAtEarlyLeaveThresholdDoesNotReturnEarlyLeave() {
        AttendanceStatus status = evaluator.evaluateCheckOut(LocalTime.of(16, 0));

        assertThat(status).isEqualTo(AttendanceStatus.PRESENT);
    }
}
