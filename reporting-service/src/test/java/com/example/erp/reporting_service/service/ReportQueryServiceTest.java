package com.example.erp.reporting_service.service;

import com.example.erp.reporting_service.projection.DepartmentReportView;
import com.example.erp.reporting_service.projection.EmployeeReportView;
import com.example.erp.reporting_service.repository.AttendanceMonthlyReportViewRepository;
import com.example.erp.reporting_service.repository.DepartmentReportViewRepository;
import com.example.erp.reporting_service.repository.EmployeeReportViewRepository;
import com.example.erp.reporting_service.web.dto.DepartmentHeadcountRow;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportQueryServiceTest {

    @Mock
    private EmployeeReportViewRepository employeeRepository;

    @Mock
    private DepartmentReportViewRepository departmentRepository;

    @Mock
    private AttendanceMonthlyReportViewRepository attendanceRepository;

    @Test
    void headcountReportCountsActiveEmployeesByDepartment() {
        UUID engineeringId = UUID.randomUUID();
        UUID financeId = UUID.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();
        when(departmentRepository.findAll()).thenReturn(List.of(
                new DepartmentReportView(engineeringId, "Engineering", UUID.randomUUID(), now),
                new DepartmentReportView(financeId, "Finance", null, now)
        ));
        when(employeeRepository.findByStatus("ACTIVE")).thenReturn(List.of(
                new EmployeeReportView(UUID.randomUUID(), engineeringId, "ACTIVE", "Engineer", now),
                new EmployeeReportView(UUID.randomUUID(), engineeringId, "ACTIVE", "Engineer", now),
                new EmployeeReportView(UUID.randomUUID(), financeId, "ACTIVE", "Analyst", now)
        ));
        ReportQueryService service = new ReportQueryService(
                employeeRepository, departmentRepository, attendanceRepository);

        List<DepartmentHeadcountRow> result = service.departmentHeadcount();

        assertThat(result).extracting(DepartmentHeadcountRow::departmentName)
                .containsExactly("Engineering", "Finance");
        assertThat(result).extracting(DepartmentHeadcountRow::activeEmployees)
                .containsExactly(2L, 1L);
    }
}
