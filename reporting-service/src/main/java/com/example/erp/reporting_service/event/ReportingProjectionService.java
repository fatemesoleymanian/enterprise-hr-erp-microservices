package com.example.erp.reporting_service.event;

import com.example.erp.reporting_service.projection.AttendanceMonthlyReportView;
import com.example.erp.reporting_service.projection.AttendanceReportContribution;
import com.example.erp.reporting_service.projection.DepartmentReportView;
import com.example.erp.reporting_service.projection.EmployeeReportView;
import com.example.erp.reporting_service.projection.ProcessedReportingEvent;
import com.example.erp.reporting_service.repository.AttendanceMonthlyReportViewRepository;
import com.example.erp.reporting_service.repository.AttendanceReportContributionRepository;
import com.example.erp.reporting_service.repository.DepartmentReportViewRepository;
import com.example.erp.reporting_service.repository.EmployeeReportViewRepository;
import com.example.erp.reporting_service.repository.ProcessedReportingEventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class ReportingProjectionService {

    private static final String UNKNOWN = "UNKNOWN";

    private final ObjectMapper objectMapper;
    private final EmployeeReportViewRepository employeeRepository;
    private final DepartmentReportViewRepository departmentRepository;
    private final AttendanceMonthlyReportViewRepository monthlyAttendanceRepository;
    private final AttendanceReportContributionRepository contributionRepository;
    private final ProcessedReportingEventRepository processedEventRepository;

    public ReportingProjectionService(
            ObjectMapper objectMapper,
            EmployeeReportViewRepository employeeRepository,
            DepartmentReportViewRepository departmentRepository,
            AttendanceMonthlyReportViewRepository monthlyAttendanceRepository,
            AttendanceReportContributionRepository contributionRepository,
            ProcessedReportingEventRepository processedEventRepository
    ) {
        this.objectMapper = objectMapper;
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
        this.monthlyAttendanceRepository = monthlyAttendanceRepository;
        this.contributionRepository = contributionRepository;
        this.processedEventRepository = processedEventRepository;
    }

    @Transactional
    public void process(ReportingDomainEvent event) {
        if (processedEventRepository.existsById(event.eventId())) {
            return;
        }

        switch (event.eventType()) {
            case "DepartmentCreated" -> handleDepartmentCreated(payload(event, DepartmentCreatedPayload.class));
            case "DepartmentManagerAssigned" -> handleDepartmentManagerAssigned(
                    payload(event, DepartmentManagerAssignedPayload.class));
            case "EmployeeCreated" -> handleEmployeeCreated(payload(event, EmployeeCreatedPayload.class));
            case "EmployeeDepartmentChanged" -> handleEmployeeDepartmentChanged(
                    payload(event, EmployeeDepartmentChangedPayload.class));
            case "EmployeeStatusChanged" -> handleEmployeeStatusChanged(
                    payload(event, EmployeeStatusChangedPayload.class));
            case "AttendanceCheckedIn" -> handleAttendanceCheckedIn(
                    payload(event, AttendanceCheckedInPayload.class));
            case "AttendanceCheckedOut" -> handleAttendanceCheckedOut(
                    payload(event, AttendanceCheckedOutPayload.class));
            case "AttendanceViolationDetected" -> handleAttendanceViolation(
                    payload(event, AttendanceViolationDetectedPayload.class));
            default -> {
                // Other events may share these topics and do not affect reporting projections.
            }
        }

        processedEventRepository.save(new ProcessedReportingEvent(
                event.eventId(),
                event.eventType(),
                OffsetDateTime.now()
        ));
    }

    private void handleEmployeeCreated(EmployeeCreatedPayload payload) {
        EmployeeReportView view = employeeRepository.findById(payload.employeeId())
                .orElseGet(() -> new EmployeeReportView(
                        payload.employeeId(),
                        payload.departmentId(),
                        payload.status(),
                        payload.jobTitle(),
                        payload.createdAt()
                ));

        view.setJobTitle(payload.jobTitle());
        if (!payload.createdAt().isBefore(view.getUpdatedAt()) || UNKNOWN.equals(view.getStatus())) {
            view.setDepartmentId(payload.departmentId());
            view.setStatus(payload.status());
        }
        view.setUpdatedAt(latest(view.getUpdatedAt(), payload.createdAt()));
        employeeRepository.save(view);
    }

    private void handleEmployeeDepartmentChanged(EmployeeDepartmentChangedPayload payload) {
        EmployeeReportView view = employeeRepository.findById(payload.employeeId())
                .orElseGet(() -> new EmployeeReportView(
                        payload.employeeId(), payload.departmentId(), UNKNOWN, null, payload.changedAt()));
        if (!payload.changedAt().isBefore(view.getUpdatedAt())) {
            view.setDepartmentId(payload.departmentId());
            view.setUpdatedAt(payload.changedAt());
        }
        employeeRepository.save(view);
    }

    private void handleEmployeeStatusChanged(EmployeeStatusChangedPayload payload) {
        EmployeeReportView view = employeeRepository.findById(payload.employeeId())
                .orElseGet(() -> new EmployeeReportView(
                        payload.employeeId(), null, payload.status(), null, payload.changedAt()));
        if (!payload.changedAt().isBefore(view.getUpdatedAt())) {
            view.setStatus(payload.status());
            view.setUpdatedAt(payload.changedAt());
        }
        employeeRepository.save(view);
    }

    private void handleDepartmentCreated(DepartmentCreatedPayload payload) {
        DepartmentReportView view = departmentRepository.findById(payload.departmentId())
                .orElseGet(() -> new DepartmentReportView(
                        payload.departmentId(), payload.name(), payload.managerUserId(), payload.createdAt()));
        view.setName(payload.name());
        if (!payload.createdAt().isBefore(view.getUpdatedAt())) {
            view.setManagerUserId(payload.managerUserId());
        }
        view.setUpdatedAt(latest(view.getUpdatedAt(), payload.createdAt()));
        departmentRepository.save(view);
    }

    private void handleDepartmentManagerAssigned(DepartmentManagerAssignedPayload payload) {
        DepartmentReportView view = departmentRepository.findById(payload.departmentId())
                .orElseGet(() -> new DepartmentReportView(
                        payload.departmentId(), "Unknown", payload.managerUserId(), payload.assignedAt()));
        if (!payload.assignedAt().isBefore(view.getUpdatedAt())) {
            view.setManagerUserId(payload.managerUserId());
            view.setUpdatedAt(payload.assignedAt());
        }
        departmentRepository.save(view);
    }

    private void handleAttendanceCheckedIn(AttendanceCheckedInPayload payload) {
        updateAttendanceContribution(
                payload.attendanceRecordId(),
                payload.employeeId(),
                payload.attendanceDate(),
                payload.statuses(),
                null,
                payload.checkInAt()
        );
    }

    private void handleAttendanceCheckedOut(AttendanceCheckedOutPayload payload) {
        updateAttendanceContribution(
                payload.attendanceRecordId(),
                payload.employeeId(),
                payload.attendanceDate(),
                payload.statuses(),
                payload.workedMinutes(),
                payload.checkOutAt()
        );
    }

    private void handleAttendanceViolation(AttendanceViolationDetectedPayload payload) {
        updateAttendanceContribution(
                payload.attendanceRecordId(),
                payload.employeeId(),
                payload.attendanceDate(),
                List.of(payload.violationType()),
                null,
                payload.detectedAt()
        );
    }

    private void updateAttendanceContribution(
            java.util.UUID attendanceRecordId,
            java.util.UUID employeeId,
            java.time.LocalDate attendanceDate,
            List<String> statuses,
            Integer workedMinutes,
            OffsetDateTime updatedAt
    ) {
        AttendanceReportContribution contribution = contributionRepository.findById(attendanceRecordId)
                .orElseGet(() -> new AttendanceReportContribution(
                        attendanceRecordId, employeeId, attendanceDate, updatedAt));
        ContributionSnapshot before = ContributionSnapshot.from(contribution);

        contribution.setEmployeeId(employeeId);
        contribution.setAttendanceDate(attendanceDate);
        contribution.setPresent(contribution.isPresent() || statuses.contains("PRESENT"));
        contribution.setLate(contribution.isLate() || statuses.contains("LATE"));
        contribution.setEarlyLeave(contribution.isEarlyLeave() || statuses.contains("EARLY_LEAVE"));
        contribution.setAbsent(contribution.isAbsent() || statuses.contains("ABSENT"));
        if (workedMinutes != null) {
            contribution.setWorkedMinutes(workedMinutes);
        }
        contribution.setUpdatedAt(latest(contribution.getUpdatedAt(), updatedAt));
        contributionRepository.save(contribution);

        AttendanceMonthlyReportView monthly = monthlyAttendanceRepository
                .findByEmployeeIdAndReportYearAndReportMonth(
                        employeeId, attendanceDate.getYear(), attendanceDate.getMonthValue())
                .orElseGet(() -> new AttendanceMonthlyReportView(
                        employeeId,
                        attendanceDate.getYear(),
                        attendanceDate.getMonthValue(),
                        0, 0, 0, 0, 0,
                        updatedAt
                ));

        monthly.setPresentDays(nonNegative(monthly.getPresentDays() + flag(contribution.isPresent()) - before.present()));
        monthly.setLateDays(nonNegative(monthly.getLateDays() + flag(contribution.isLate()) - before.late()));
        monthly.setEarlyLeaveDays(nonNegative(
                monthly.getEarlyLeaveDays() + flag(contribution.isEarlyLeave()) - before.earlyLeave()));
        monthly.setAbsentDays(nonNegative(monthly.getAbsentDays() + flag(contribution.isAbsent()) - before.absent()));
        monthly.setTotalWorkedMinutes(nonNegative(
                monthly.getTotalWorkedMinutes() + contribution.getWorkedMinutes() - before.workedMinutes()));
        monthly.setUpdatedAt(latest(monthly.getUpdatedAt(), updatedAt));
        monthlyAttendanceRepository.save(monthly);
    }

    private <T> T payload(ReportingDomainEvent event, Class<T> payloadType) {
        return objectMapper.convertValue(event.payload(), payloadType);
    }

    private static OffsetDateTime latest(OffsetDateTime current, OffsetDateTime candidate) {
        return current == null || candidate.isAfter(current) ? candidate : current;
    }

    private static int flag(boolean value) {
        return value ? 1 : 0;
    }

    private static int nonNegative(int value) {
        return Math.max(value, 0);
    }

    private record ContributionSnapshot(int present, int late, int earlyLeave, int absent, int workedMinutes) {

        private static ContributionSnapshot from(AttendanceReportContribution contribution) {
            return new ContributionSnapshot(
                    flag(contribution.isPresent()),
                    flag(contribution.isLate()),
                    flag(contribution.isEarlyLeave()),
                    flag(contribution.isAbsent()),
                    contribution.getWorkedMinutes()
            );
        }
    }
}
