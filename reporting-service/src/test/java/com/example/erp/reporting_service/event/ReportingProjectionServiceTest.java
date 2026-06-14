package com.example.erp.reporting_service.event;

import com.example.erp.reporting_service.projection.AttendanceMonthlyReportView;
import com.example.erp.reporting_service.projection.AttendanceReportContribution;
import com.example.erp.reporting_service.projection.DepartmentReportView;
import com.example.erp.reporting_service.projection.EmployeeReportView;
import com.example.erp.reporting_service.repository.AttendanceMonthlyReportViewRepository;
import com.example.erp.reporting_service.repository.AttendanceReportContributionRepository;
import com.example.erp.reporting_service.repository.DepartmentReportViewRepository;
import com.example.erp.reporting_service.repository.EmployeeReportViewRepository;
import com.example.erp.reporting_service.repository.ProcessedReportingEventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportingProjectionServiceTest {

    @Mock
    private EmployeeReportViewRepository employeeRepository;

    @Mock
    private DepartmentReportViewRepository departmentRepository;

    @Mock
    private AttendanceMonthlyReportViewRepository monthlyAttendanceRepository;

    @Mock
    private AttendanceReportContributionRepository contributionRepository;

    @Mock
    private ProcessedReportingEventRepository processedEventRepository;

    private ObjectMapper objectMapper;
    private ReportingProjectionService projectionService;

    @BeforeEach
    void setUp() {
        objectMapper = JsonMapper.builder().addModule(new JavaTimeModule()).build();
        projectionService = new ReportingProjectionService(
                objectMapper,
                employeeRepository,
                departmentRepository,
                monthlyAttendanceRepository,
                contributionRepository,
                processedEventRepository
        );
    }

    @Test
    void employeeCreatedCreatesEmployeeProjection() {
        UUID employeeId = UUID.randomUUID();
        UUID departmentId = UUID.randomUUID();
        OffsetDateTime createdAt = time(2026, 6, 1, 8, 0);
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.empty());

        projectionService.process(event("EmployeeCreated", new EmployeeCreatedPayload(
                employeeId, departmentId, "ACTIVE", "Engineer", createdAt)));

        ArgumentCaptor<EmployeeReportView> captor = ArgumentCaptor.forClass(EmployeeReportView.class);
        verify(employeeRepository).save(captor.capture());
        assertThat(captor.getValue().getEmployeeId()).isEqualTo(employeeId);
        assertThat(captor.getValue().getDepartmentId()).isEqualTo(departmentId);
        assertThat(captor.getValue().getStatus()).isEqualTo("ACTIVE");
        assertThat(captor.getValue().getJobTitle()).isEqualTo("Engineer");
    }

    @Test
    void employeeDepartmentChangedUpdatesEmployeeProjection() {
        UUID employeeId = UUID.randomUUID();
        UUID originalDepartmentId = UUID.randomUUID();
        UUID newDepartmentId = UUID.randomUUID();
        EmployeeReportView view = new EmployeeReportView(
                employeeId, originalDepartmentId, "ACTIVE", "Engineer", time(2026, 6, 1, 8, 0));
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(view));

        projectionService.process(event("EmployeeDepartmentChanged", new EmployeeDepartmentChangedPayload(
                employeeId, newDepartmentId, time(2026, 6, 2, 8, 0))));

        assertThat(view.getDepartmentId()).isEqualTo(newDepartmentId);
        verify(employeeRepository).save(view);
    }

    @Test
    void departmentCreatedCreatesDepartmentProjection() {
        UUID departmentId = UUID.randomUUID();
        UUID managerId = UUID.randomUUID();
        when(departmentRepository.findById(departmentId)).thenReturn(Optional.empty());

        projectionService.process(event("DepartmentCreated", new DepartmentCreatedPayload(
                departmentId, "Engineering", managerId, time(2026, 6, 1, 8, 0))));

        ArgumentCaptor<DepartmentReportView> captor = ArgumentCaptor.forClass(DepartmentReportView.class);
        verify(departmentRepository).save(captor.capture());
        assertThat(captor.getValue().getDepartmentId()).isEqualTo(departmentId);
        assertThat(captor.getValue().getName()).isEqualTo("Engineering");
        assertThat(captor.getValue().getManagerUserId()).isEqualTo(managerId);
    }

    @Test
    void attendanceCheckedOutUpdatesMonthlyAttendanceProjection() {
        UUID attendanceRecordId = UUID.randomUUID();
        UUID employeeId = UUID.randomUUID();
        LocalDate attendanceDate = LocalDate.of(2026, 6, 3);
        when(contributionRepository.findById(attendanceRecordId)).thenReturn(Optional.empty());
        when(monthlyAttendanceRepository.findByEmployeeIdAndReportYearAndReportMonth(employeeId, 2026, 6))
                .thenReturn(Optional.empty());

        projectionService.process(event("AttendanceCheckedOut", new AttendanceCheckedOutPayload(
                attendanceRecordId,
                employeeId,
                attendanceDate,
                time(2026, 6, 3, 8, 0),
                time(2026, 6, 3, 16, 0),
                List.of("PRESENT", "LATE"),
                450
        )));

        ArgumentCaptor<AttendanceReportContribution> contributionCaptor =
                ArgumentCaptor.forClass(AttendanceReportContribution.class);
        verify(contributionRepository).save(contributionCaptor.capture());
        assertThat(contributionCaptor.getValue().isPresent()).isTrue();
        assertThat(contributionCaptor.getValue().isLate()).isTrue();

        ArgumentCaptor<AttendanceMonthlyReportView> monthlyCaptor =
                ArgumentCaptor.forClass(AttendanceMonthlyReportView.class);
        verify(monthlyAttendanceRepository).save(monthlyCaptor.capture());
        assertThat(monthlyCaptor.getValue().getPresentDays()).isEqualTo(1);
        assertThat(monthlyCaptor.getValue().getLateDays()).isEqualTo(1);
        assertThat(monthlyCaptor.getValue().getTotalWorkedMinutes()).isEqualTo(450);
    }

    @Test
    void duplicateEventDoesNotUpdateProjectionAgain() {
        ReportingDomainEvent event = event("EmployeeCreated", new EmployeeCreatedPayload(
                UUID.randomUUID(), UUID.randomUUID(), "ACTIVE", "Engineer", time(2026, 6, 1, 8, 0)));
        when(processedEventRepository.existsById(event.eventId())).thenReturn(true);

        projectionService.process(event);

        verify(employeeRepository, never()).save(any());
        verify(processedEventRepository, never()).save(any());
    }

    private ReportingDomainEvent event(String eventType, Object payload) {
        return new ReportingDomainEvent(
                UUID.randomUUID(),
                eventType,
                time(2026, 6, 1, 8, 0),
                1,
                "test-service",
                UUID.randomUUID(),
                objectMapper.valueToTree(payload)
        );
    }

    private static OffsetDateTime time(int year, int month, int day, int hour, int minute) {
        return OffsetDateTime.of(year, month, day, hour, minute, 0, 0, ZoneOffset.UTC);
    }
}
