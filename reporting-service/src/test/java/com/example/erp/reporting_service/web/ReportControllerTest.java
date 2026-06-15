package com.example.erp.reporting_service.web;

import com.example.erp.reporting_service.service.ReportQueryService;
import com.example.erp.reporting_service.web.dto.AttendanceTotals;
import com.example.erp.reporting_service.web.dto.DepartmentAttendanceReport;
import com.example.erp.reporting_service.web.dto.DepartmentHeadcountRow;
import com.example.erp.reporting_service.web.dto.EmployeeStatusSummary;
import com.example.erp.reporting_service.web.dto.MonthlyAttendanceReportRow;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReportController.class)
@Import(ReportExceptionHandler.class)
class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReportQueryService reportQueryService;

    @Test
    void monthlyAttendanceReturnsApiResponse() throws Exception {
        UUID employeeId = UUID.randomUUID();
        UUID departmentId = UUID.randomUUID();
        when(reportQueryService.monthlyAttendance(2026, 6, employeeId, departmentId)).thenReturn(List.of(
                new MonthlyAttendanceReportRow(employeeId, departmentId, 2026, 6, 20, 2, 1, 0, 9600)
        ));

        mockMvc.perform(get("/api/reports/attendance/monthly")
                        .queryParam("year", "2026")
                        .queryParam("month", "6")
                        .queryParam("employeeId", employeeId.toString())
                        .queryParam("departmentId", departmentId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.data[0].employeeId").value(employeeId.toString()))
                .andExpect(jsonPath("$.data[0].totalWorkedMinutes").value(9600));
    }

    @Test
    void departmentAttendanceReturnsTotals() throws Exception {
        UUID departmentId = UUID.randomUUID();
        when(reportQueryService.departmentAttendance(departmentId, 2026, 6)).thenReturn(
                new DepartmentAttendanceReport(
                        departmentId, 2026, 6, List.of(), new AttendanceTotals(10, 2, 1, 0, 4800)));

        mockMvc.perform(get("/api/reports/departments/{departmentId}/attendance", departmentId)
                        .queryParam("year", "2026")
                        .queryParam("month", "6"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.departmentId").value(departmentId.toString()))
                .andExpect(jsonPath("$.data.totals.presentDays").value(10));
    }

    @Test
    void employeeStatusSummaryReturnsCounts() throws Exception {
        when(reportQueryService.employeeStatusSummary(null)).thenReturn(
                new EmployeeStatusSummary(null, 4, 3, 1, Map.of("ACTIVE", 3L, "ON_LEAVE", 1L)));

        mockMvc.perform(get("/api/reports/employees/status-summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalEmployees").value(4))
                .andExpect(jsonPath("$.data.statusCounts.ACTIVE").value(3));
    }

    @Test
    void departmentHeadcountReturnsRows() throws Exception {
        UUID departmentId = UUID.randomUUID();
        when(reportQueryService.departmentHeadcount()).thenReturn(List.of(
                new DepartmentHeadcountRow(departmentId, "Engineering", null, 7)
        ));

        mockMvc.perform(get("/api/reports/departments/headcount"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].departmentName").value("Engineering"))
                .andExpect(jsonPath("$.data[0].activeEmployees").value(7));
    }

    @Test
    void invalidMonthReturnsStandardErrorResponse() throws Exception {
        mockMvc.perform(get("/api/reports/attendance/monthly")
                        .queryParam("year", "2026")
                        .queryParam("month", "13"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("INVALID_REPORT_FILTER"))
                .andExpect(jsonPath("$.path").value("/api/reports/attendance/monthly"));
    }

    @Test
    void missingYearReturnsStandardErrorResponse() throws Exception {
        mockMvc.perform(get("/api/reports/attendance/monthly")
                        .queryParam("month", "6"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("INVALID_REPORT_FILTER"));
    }
}
