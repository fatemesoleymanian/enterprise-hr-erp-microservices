package com.example.erp.attendance.service;

import com.example.erp.attendance.domain.AttendancePolicyEvaluator;
import com.example.erp.attendance.domain.AttendanceRecord;
import com.example.erp.attendance.domain.AttendanceStatus;
import com.example.erp.attendance.event.AttendanceEventPublisher;
import com.example.erp.attendance.event.AttendanceEventTypes;
import com.example.erp.attendance.event.DomainEvent;
import com.example.erp.attendance.repository.AttendanceRecordRepository;
import com.example.erp.attendance.web.dto.AttendanceRecordResponse;
import com.example.erp.attendance.web.dto.CheckInRequest;
import com.example.erp.attendance.web.dto.CheckOutRequest;
import com.example.erp.attendance.web.dto.MonthlyAttendanceSummaryResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AttendanceServiceImplTest {

    @Mock
    private AttendanceRecordRepository attendanceRecordRepository;

    @Mock
    private AttendanceEventPublisher attendanceEventPublisher;

    private AttendanceServiceImpl attendanceService;

    @BeforeEach
    void setUp() {
        attendanceService = new AttendanceServiceImpl(
                attendanceRecordRepository,
                new AttendancePolicyEvaluator(),
                attendanceEventPublisher
        );
    }

    @Test
    void firstCheckInCreatesRecordAndPublishesCheckedInEvent() {
        UUID employeeId = UUID.randomUUID();
        OffsetDateTime checkInAt = OffsetDateTime.of(2026, 6, 1, 9, 0, 0, 0, ZoneOffset.UTC);
        when(attendanceRecordRepository.findByEmployeeIdAndAttendanceDate(employeeId, checkInAt.toLocalDate()))
                .thenReturn(Optional.empty());
        when(attendanceRecordRepository.save(any(AttendanceRecord.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AttendanceRecordResponse response = attendanceService.checkIn(new CheckInRequest(employeeId, checkInAt));

        assertThat(response.employeeId()).isEqualTo(employeeId);
        assertThat(response.attendanceDate()).isEqualTo(LocalDate.of(2026, 6, 1));
        assertThat(response.statuses()).containsExactly(AttendanceStatus.PRESENT);

        ArgumentCaptor<DomainEvent<?>> eventCaptor = ArgumentCaptor.forClass(DomainEvent.class);
        verify(attendanceEventPublisher).publish(eventCaptor.capture());
        assertThat(eventCaptor.getValue().eventType()).isEqualTo(AttendanceEventTypes.ATTENDANCE_CHECKED_IN);
    }

    @Test
    void duplicateCheckInThrowsConflictAndDoesNotPublishEvent() {
        UUID employeeId = UUID.randomUUID();
        OffsetDateTime checkInAt = OffsetDateTime.of(2026, 6, 1, 9, 0, 0, 0, ZoneOffset.UTC);
        AttendanceRecord existingRecord = new AttendanceRecord(
                employeeId,
                checkInAt.toLocalDate(),
                checkInAt,
                AttendanceStatus.PRESENT
        );
        when(attendanceRecordRepository.findByEmployeeIdAndAttendanceDate(employeeId, checkInAt.toLocalDate()))
                .thenReturn(Optional.of(existingRecord));

        assertThatThrownBy(() -> attendanceService.checkIn(new CheckInRequest(employeeId, checkInAt)))
                .isInstanceOf(AttendanceConflictException.class)
                .hasMessage("Employee already checked in for this date.");

        verify(attendanceRecordRepository, never()).save(any(AttendanceRecord.class));
        verify(attendanceEventPublisher, never()).publish(any());
    }

    @Test
    void checkOutWithoutCheckInThrowsConflictAndDoesNotPublishEvent() {
        UUID employeeId = UUID.randomUUID();
        OffsetDateTime checkOutAt = OffsetDateTime.of(2026, 6, 1, 16, 30, 0, 0, ZoneOffset.UTC);
        when(attendanceRecordRepository.findByEmployeeIdAndAttendanceDate(employeeId, checkOutAt.toLocalDate()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> attendanceService.checkOut(new CheckOutRequest(employeeId, checkOutAt)))
                .isInstanceOf(AttendanceConflictException.class)
                .hasMessage("Employee must check in before checking out.");

        verify(attendanceRecordRepository, never()).save(any(AttendanceRecord.class));
        verify(attendanceEventPublisher, never()).publish(any());
    }

    @Test
    void lateCheckInPublishesViolationEvent() {
        UUID employeeId = UUID.randomUUID();
        OffsetDateTime checkInAt = OffsetDateTime.of(2026, 6, 1, 9, 20, 0, 0, ZoneOffset.UTC);
        when(attendanceRecordRepository.findByEmployeeIdAndAttendanceDate(employeeId, checkInAt.toLocalDate()))
                .thenReturn(Optional.empty());
        when(attendanceRecordRepository.save(any(AttendanceRecord.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AttendanceRecordResponse response = attendanceService.checkIn(new CheckInRequest(employeeId, checkInAt));

        assertThat(response.statuses()).containsExactly(AttendanceStatus.LATE);

        ArgumentCaptor<DomainEvent<?>> eventCaptor = ArgumentCaptor.forClass(DomainEvent.class);
        verify(attendanceEventPublisher, org.mockito.Mockito.times(2)).publish(eventCaptor.capture());
        assertThat(eventCaptor.getAllValues())
                .extracting(DomainEvent::eventType)
                .containsExactly(
                        AttendanceEventTypes.ATTENDANCE_CHECKED_IN,
                        AttendanceEventTypes.ATTENDANCE_VIOLATION_DETECTED
                );
    }

    @Test
    void checkOutCalculatesWorkedMinutesAndPublishesCheckedOutEvent() {
        UUID employeeId = UUID.randomUUID();
        OffsetDateTime checkInAt = OffsetDateTime.of(2026, 6, 1, 9, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime checkOutAt = OffsetDateTime.of(2026, 6, 1, 16, 30, 0, 0, ZoneOffset.UTC);
        AttendanceRecord record = new AttendanceRecord(
                employeeId,
                checkInAt.toLocalDate(),
                checkInAt,
                AttendanceStatus.PRESENT
        );
        when(attendanceRecordRepository.findByEmployeeIdAndAttendanceDate(employeeId, checkOutAt.toLocalDate()))
                .thenReturn(Optional.of(record));
        when(attendanceRecordRepository.save(any(AttendanceRecord.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AttendanceRecordResponse response = attendanceService.checkOut(new CheckOutRequest(employeeId, checkOutAt));

        assertThat(response.checkOutAt()).isEqualTo(checkOutAt);
        assertThat(response.workedMinutes()).isEqualTo(450);

        ArgumentCaptor<DomainEvent<?>> eventCaptor = ArgumentCaptor.forClass(DomainEvent.class);
        verify(attendanceEventPublisher).publish(eventCaptor.capture());
        assertThat(eventCaptor.getValue().eventType()).isEqualTo(AttendanceEventTypes.ATTENDANCE_CHECKED_OUT);
    }

    @Test
    void monthlySummaryReturnsAggregatedAttendanceCounts() {
        UUID employeeId = UUID.randomUUID();
        AttendanceRecord presentRecord = checkedOutRecord(
                employeeId,
                OffsetDateTime.of(2026, 6, 1, 9, 0, 0, 0, ZoneOffset.UTC),
                OffsetDateTime.of(2026, 6, 1, 17, 0, 0, 0, ZoneOffset.UTC),
                AttendanceStatus.PRESENT
        );
        AttendanceRecord lateRecord = checkedOutRecord(
                employeeId,
                OffsetDateTime.of(2026, 6, 2, 9, 20, 0, 0, ZoneOffset.UTC),
                OffsetDateTime.of(2026, 6, 2, 17, 0, 0, 0, ZoneOffset.UTC),
                AttendanceStatus.LATE
        );
        AttendanceRecord earlyLeaveRecord = checkedOutRecord(
                employeeId,
                OffsetDateTime.of(2026, 6, 3, 9, 0, 0, 0, ZoneOffset.UTC),
                OffsetDateTime.of(2026, 6, 3, 15, 30, 0, 0, ZoneOffset.UTC),
                AttendanceStatus.EARLY_LEAVE
        );
        when(attendanceRecordRepository.findByEmployeeIdAndAttendanceDateBetweenOrderByAttendanceDateAsc(
                employeeId,
                LocalDate.of(2026, 6, 1),
                LocalDate.of(2026, 6, 30)
        )).thenReturn(List.of(presentRecord, lateRecord, earlyLeaveRecord));

        MonthlyAttendanceSummaryResponse summary = attendanceService.monthlySummary(employeeId, 2026, 6);

        assertThat(summary.presentDays()).isEqualTo(3);
        assertThat(summary.lateDays()).isEqualTo(1);
        assertThat(summary.earlyLeaveDays()).isEqualTo(1);
        assertThat(summary.absentDays()).isEqualTo(27);
        assertThat(summary.totalWorkedMinutes()).isEqualTo(480 + 460 + 390);
    }

    private static AttendanceRecord checkedOutRecord(
            UUID employeeId,
            OffsetDateTime checkInAt,
            OffsetDateTime checkOutAt,
            AttendanceStatus status
    ) {
        AttendanceRecord record = new AttendanceRecord(employeeId, checkInAt.toLocalDate(), checkInAt, status);
        record.checkOut(
                checkOutAt,
                status,
                Math.toIntExact(java.time.temporal.ChronoUnit.MINUTES.between(checkInAt, checkOutAt))
        );
        return record;
    }
}
