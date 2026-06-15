package com.example.erp.reporting_service.web;

import com.example.erp.common.api.ApiResponse;
import com.example.erp.reporting_service.service.ReportQueryService;
import com.example.erp.reporting_service.web.dto.DepartmentAttendanceReport;
import com.example.erp.reporting_service.web.dto.DepartmentHeadcountRow;
import com.example.erp.reporting_service.web.dto.EmployeeStatusSummary;
import com.example.erp.reporting_service.web.dto.MonthlyAttendanceReportRow;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Validated
@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportQueryService reportQueryService;

    public ReportController(ReportQueryService reportQueryService) {
        this.reportQueryService = reportQueryService;
    }

    @GetMapping("/attendance/monthly")
    public ApiResponse<List<MonthlyAttendanceReportRow>> monthlyAttendance(
            @RequestParam @Min(2000) @Max(2100) int year,
            @RequestParam @Min(1) @Max(12) int month,
            @RequestParam(required = false) UUID employeeId,
            @RequestParam(required = false) UUID departmentId
    ) {
        return ApiResponse.success(reportQueryService.monthlyAttendance(year, month, employeeId, departmentId));
    }

    @GetMapping("/departments/{departmentId}/attendance")
    public ApiResponse<DepartmentAttendanceReport> departmentAttendance(
            @PathVariable UUID departmentId,
            @RequestParam @Min(2000) @Max(2100) int year,
            @RequestParam @Min(1) @Max(12) int month
    ) {
        return ApiResponse.success(reportQueryService.departmentAttendance(departmentId, year, month));
    }

    @GetMapping("/employees/status-summary")
    public ApiResponse<EmployeeStatusSummary> employeeStatusSummary(
            @RequestParam(required = false) UUID departmentId
    ) {
        return ApiResponse.success(reportQueryService.employeeStatusSummary(departmentId));
    }

    @GetMapping("/departments/headcount")
    public ApiResponse<List<DepartmentHeadcountRow>> departmentHeadcount() {
        return ApiResponse.success(reportQueryService.departmentHeadcount());
    }
}
