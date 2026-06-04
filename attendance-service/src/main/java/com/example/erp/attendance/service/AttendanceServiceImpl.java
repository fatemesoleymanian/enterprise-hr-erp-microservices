package com.example.erp.attendance.service;

import com.example.erp.attendance.domain.AttendancePolicyEvaluator;
import com.example.erp.attendance.domain.AttendanceRecord;
import com.example.erp.attendance.domain.AttendanceStatus;
import com.example.erp.attendance.event.AttendanceCheckedInPayload;
import com.example.erp.attendance.event.AttendanceCheckedOutPayload;
import com.example.erp.attendance.event.AttendanceEventPublisher;
import com.example.erp.attendance.event.AttendanceEventTypes;
import com.example.erp.attendance.event.AttendanceViolationDetectedPayload;
import com.example.erp.attendance.event.DomainEvent;
import com.example.erp.attendance.repository.AttendanceRecordRepository;
import com.example.erp.attendance.web.dto.AttendanceRecordResponse;
import com.example.erp.attendance.web.dto.CheckInRequest;
import com.example.erp.attendance.web.dto.CheckOutRequest;
import com.example.erp.attendance.web.dto.MonthlyAttendanceSummaryResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
public class AttendanceServiceImpl implements AttendanceService {

    private static final LocalDate MIN_ATTENDANCE_DATE = LocalDate.of(1900, 1, 1);
    private static final LocalDate MAX_ATTENDANCE_DATE = LocalDate.of(2100, 12, 31);

    private final AttendanceRecordRepository attendanceRecordRepository;
    private final AttendancePolicyEvaluator attendancePolicyEvaluator;
    private final AttendanceEventPublisher attendanceEventPublisher;

    public AttendanceServiceImpl(
            AttendanceRecordRepository attendanceRecordRepository,
            AttendancePolicyEvaluator attendancePolicyEvaluator,
            AttendanceEventPublisher attendanceEventPublisher
    ) {
        this.attendanceRecordRepository = attendanceRecordRepository;
        this.attendancePolicyEvaluator = attendancePolicyEvaluator;
        this.attendanceEventPublisher = attendanceEventPublisher;
    }

    @Override
    @Transactional
    public AttendanceRecordResponse checkIn(CheckInRequest request) {
        LocalDate attendanceDate = request.checkInAt().toLocalDate();
        attendanceRecordRepository.findByEmployeeIdAndAttendanceDate(request.employeeId(), attendanceDate)
                .ifPresent(record -> {
                    throw new AttendanceConflictException(
                            "DUPLICATE_CHECK_IN",
                            "Employee already checked in for this date."
                    );
                });

        AttendanceStatus status = attendancePolicyEvaluator.evaluateCheckIn(request.checkInAt().toLocalTime());
        AttendanceRecord savedRecord = attendanceRecordRepository.save(
                new AttendanceRecord(request.employeeId(), attendanceDate, request.checkInAt(), status)
        );

        publishCheckedIn(savedRecord);
        publishViolationIfNeeded(savedRecord, status, request.checkInAt());

        return AttendanceRecordResponse.from(savedRecord);
    }

    @Override
    @Transactional
    public AttendanceRecordResponse checkOut(CheckOutRequest request) {
        LocalDate attendanceDate = request.checkOutAt().toLocalDate();
        AttendanceRecord record = attendanceRecordRepository.findByEmployeeIdAndAttendanceDate(
                request.employeeId(),
                attendanceDate
        ).orElseThrow(() -> new AttendanceConflictException(
                "CHECK_IN_NOT_FOUND",
                "Employee must check in before checking out."
        ));

        if (record.getCheckOutAt() != null) {
            throw new AttendanceConflictException(
                    "DUPLICATE_CHECK_OUT",
                    "Employee already checked out for this date."
            );
        }

        if (request.checkOutAt().isBefore(record.getCheckInAt())) {
            throw new AttendanceConflictException(
                    "INVALID_CHECK_OUT",
                    "Check-out time cannot be before check-in time."
            );
        }

        AttendanceStatus checkOutStatus = attendancePolicyEvaluator.evaluateCheckOut(request.checkOutAt().toLocalTime());
        int workedMinutes = Math.toIntExact(ChronoUnit.MINUTES.between(record.getCheckInAt(), request.checkOutAt()));
        record.checkOut(request.checkOutAt(), statusAfterCheckOut(record.getStatus(), checkOutStatus), workedMinutes);

        AttendanceRecord savedRecord = attendanceRecordRepository.save(record);
        publishCheckedOut(savedRecord);
        publishViolationIfNeeded(savedRecord, checkOutStatus, request.checkOutAt());

        return AttendanceRecordResponse.from(savedRecord);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceRecordResponse> findEmployeeAttendance(UUID employeeId, LocalDate from, LocalDate to) {
        List<AttendanceRecord> records;
        if (from == null && to == null) {
            records = attendanceRecordRepository.findByEmployeeIdOrderByAttendanceDateAsc(employeeId);
        } else {
            records = attendanceRecordRepository.findByEmployeeIdAndAttendanceDateBetweenOrderByAttendanceDateAsc(
                    employeeId,
                    from == null ? MIN_ATTENDANCE_DATE : from,
                    to == null ? MAX_ATTENDANCE_DATE : to
            );
        }

        return records.stream()
                .map(AttendanceRecordResponse::from)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public MonthlyAttendanceSummaryResponse monthlySummary(UUID employeeId, int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        List<AttendanceRecord> records = attendanceRecordRepository
                .findByEmployeeIdAndAttendanceDateBetweenOrderByAttendanceDateAsc(
                        employeeId,
                        yearMonth.atDay(1),
                        yearMonth.atEndOfMonth()
                );

        int presentDays = (int) records.stream()
                .filter(record -> record.getCheckInAt() != null)
                .count();
        int lateDays = countByStatus(records, AttendanceStatus.LATE);
        int earlyLeaveDays = countByStatus(records, AttendanceStatus.EARLY_LEAVE);
        int absentDays = yearMonth.lengthOfMonth() - presentDays;
        int totalWorkedMinutes = records.stream()
                .mapToInt(AttendanceRecord::getWorkedMinutes)
                .sum();

        return new MonthlyAttendanceSummaryResponse(
                employeeId,
                year,
                month,
                presentDays,
                lateDays,
                earlyLeaveDays,
                absentDays,
                totalWorkedMinutes
        );
    }

    private static int countByStatus(List<AttendanceRecord> records, AttendanceStatus status) {
        return (int) records.stream()
                .filter(record -> record.getStatus() == status)
                .count();
    }

    private static AttendanceStatus statusAfterCheckOut(AttendanceStatus existingStatus, AttendanceStatus checkOutStatus) {
        if (checkOutStatus == AttendanceStatus.PRESENT) {
            return existingStatus;
        }

        return checkOutStatus;
    }

    private void publishCheckedIn(AttendanceRecord record) {
        attendanceEventPublisher.publish(DomainEvent.attendanceEvent(
                AttendanceEventTypes.ATTENDANCE_CHECKED_IN,
                new AttendanceCheckedInPayload(
                        record.getId(),
                        record.getEmployeeId(),
                        record.getAttendanceDate(),
                        record.getCheckInAt(),
                        AttendanceRecordResponse.statusesFrom(record)
                )
        ));
    }

    private void publishCheckedOut(AttendanceRecord record) {
        attendanceEventPublisher.publish(DomainEvent.attendanceEvent(
                AttendanceEventTypes.ATTENDANCE_CHECKED_OUT,
                new AttendanceCheckedOutPayload(
                        record.getId(),
                        record.getEmployeeId(),
                        record.getAttendanceDate(),
                        record.getCheckInAt(),
                        record.getCheckOutAt(),
                        AttendanceRecordResponse.statusesFrom(record),
                        record.getWorkedMinutes()
                )
        ));
    }

    private void publishViolationIfNeeded(AttendanceRecord record, AttendanceStatus status, java.time.OffsetDateTime detectedAt) {
        if (status != AttendanceStatus.LATE && status != AttendanceStatus.EARLY_LEAVE) {
            return;
        }

        attendanceEventPublisher.publish(DomainEvent.attendanceEvent(
                AttendanceEventTypes.ATTENDANCE_VIOLATION_DETECTED,
                new AttendanceViolationDetectedPayload(
                        record.getId(),
                        record.getEmployeeId(),
                        record.getAttendanceDate(),
                        status,
                        detectedAt
                )
        ));
    }
}
