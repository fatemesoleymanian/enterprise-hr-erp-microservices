package com.example.erp.reporting_service.service;

import com.example.erp.reporting_service.projection.AttendanceMonthlyReportView;
import com.example.erp.reporting_service.projection.EmployeeReportView;
import com.example.erp.reporting_service.repository.AttendanceMonthlyReportViewRepository;
import com.example.erp.reporting_service.repository.DepartmentReportViewRepository;
import com.example.erp.reporting_service.repository.EmployeeReportViewRepository;
import com.example.erp.reporting_service.web.dto.AttendanceTotals;
import com.example.erp.reporting_service.web.dto.DepartmentAttendanceReport;
import com.example.erp.reporting_service.web.dto.DepartmentHeadcountRow;
import com.example.erp.reporting_service.web.dto.EmployeeStatusSummary;
import com.example.erp.reporting_service.web.dto.MonthlyAttendanceReportRow;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ReportQueryService {

    private static final String ACTIVE_STATUS = "ACTIVE";

    private final EmployeeReportViewRepository employeeRepository;
    private final DepartmentReportViewRepository departmentRepository;
    private final AttendanceMonthlyReportViewRepository attendanceRepository;

    public ReportQueryService(
            EmployeeReportViewRepository employeeRepository,
            DepartmentReportViewRepository departmentRepository,
            AttendanceMonthlyReportViewRepository attendanceRepository
    ) {
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
        this.attendanceRepository = attendanceRepository;
    }

    public List<MonthlyAttendanceReportRow> monthlyAttendance(
            int year,
            int month,
            UUID employeeId,
            UUID departmentId
    ) {
        List<AttendanceMonthlyReportView> views;
        if (employeeId != null) {
            views = attendanceRepository.findByEmployeeIdAndReportYearAndReportMonth(employeeId, year, month)
                    .stream()
                    .toList();
        } else if (departmentId != null) {
            List<UUID> employeeIds = employeeRepository.findByDepartmentId(departmentId).stream()
                    .map(EmployeeReportView::getEmployeeId)
                    .toList();
            views = employeeIds.isEmpty()
                    ? List.of()
                    : attendanceRepository.findByEmployeeIdInAndReportYearAndReportMonth(employeeIds, year, month);
        } else {
            views = attendanceRepository.findByReportYearAndReportMonth(year, month);
        }

        Map<UUID, EmployeeReportView> employeesById = employeeRepository
                .findAllById(views.stream().map(AttendanceMonthlyReportView::getEmployeeId).toList())
                .stream()
                .collect(Collectors.toMap(EmployeeReportView::getEmployeeId, Function.identity()));

        return views.stream()
                .filter(view -> departmentId == null
                        || departmentId.equals(departmentId(employeesById.get(view.getEmployeeId()))))
                .map(view -> toRow(view, departmentId(employeesById.get(view.getEmployeeId()))))
                .sorted(Comparator.comparing(MonthlyAttendanceReportRow::employeeId))
                .toList();
    }

    public DepartmentAttendanceReport departmentAttendance(UUID departmentId, int year, int month) {
        List<MonthlyAttendanceReportRow> employees = monthlyAttendance(year, month, null, departmentId);
        AttendanceTotals totals = new AttendanceTotals(
                employees.stream().mapToInt(MonthlyAttendanceReportRow::presentDays).sum(),
                employees.stream().mapToInt(MonthlyAttendanceReportRow::lateDays).sum(),
                employees.stream().mapToInt(MonthlyAttendanceReportRow::earlyLeaveDays).sum(),
                employees.stream().mapToInt(MonthlyAttendanceReportRow::absentDays).sum(),
                employees.stream().mapToInt(MonthlyAttendanceReportRow::totalWorkedMinutes).sum()
        );
        return new DepartmentAttendanceReport(departmentId, year, month, employees, totals);
    }

    public EmployeeStatusSummary employeeStatusSummary(UUID departmentId) {
        List<EmployeeReportView> employees = departmentId == null
                ? employeeRepository.findAll()
                : employeeRepository.findByDepartmentId(departmentId);
        Map<String, Long> statusCounts = employees.stream().collect(Collectors.groupingBy(
                EmployeeReportView::getStatus,
                TreeMap::new,
                Collectors.counting()
        ));
        long activeEmployees = statusCounts.getOrDefault(ACTIVE_STATUS, 0L);
        return new EmployeeStatusSummary(
                departmentId,
                employees.size(),
                activeEmployees,
                employees.size() - activeEmployees,
                statusCounts
        );
    }

    public List<DepartmentHeadcountRow> departmentHeadcount() {
        Map<UUID, Long> activeCounts = new HashMap<>();
        employeeRepository.findByStatus(ACTIVE_STATUS).stream()
                .map(EmployeeReportView::getDepartmentId)
                .filter(java.util.Objects::nonNull)
                .forEach(departmentId -> activeCounts.merge(departmentId, 1L, Long::sum));

        List<DepartmentHeadcountRow> rows = new ArrayList<>();
        departmentRepository.findAll().forEach(department -> rows.add(new DepartmentHeadcountRow(
                department.getDepartmentId(),
                department.getName(),
                department.getManagerUserId(),
                activeCounts.getOrDefault(department.getDepartmentId(), 0L)
        )));
        rows.sort(Comparator.comparing(DepartmentHeadcountRow::departmentName)
                .thenComparing(DepartmentHeadcountRow::departmentId));
        return rows;
    }

    private static MonthlyAttendanceReportRow toRow(AttendanceMonthlyReportView view, UUID departmentId) {
        return new MonthlyAttendanceReportRow(
                view.getEmployeeId(),
                departmentId,
                view.getReportYear(),
                view.getReportMonth(),
                view.getPresentDays(),
                view.getLateDays(),
                view.getEarlyLeaveDays(),
                view.getAbsentDays(),
                view.getTotalWorkedMinutes()
        );
    }

    private static UUID departmentId(EmployeeReportView employee) {
        return employee == null ? null : employee.getDepartmentId();
    }
}
