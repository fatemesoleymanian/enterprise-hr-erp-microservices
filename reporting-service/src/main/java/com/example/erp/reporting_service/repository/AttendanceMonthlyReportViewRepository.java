package com.example.erp.reporting_service.repository;

import com.example.erp.reporting_service.projection.AttendanceMonthlyReportView;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AttendanceMonthlyReportViewRepository extends JpaRepository<AttendanceMonthlyReportView, UUID> {

    Optional<AttendanceMonthlyReportView> findByEmployeeIdAndReportYearAndReportMonth(
            UUID employeeId,
            int reportYear,
            int reportMonth
    );

    List<AttendanceMonthlyReportView> findByReportYearAndReportMonth(int reportYear, int reportMonth);
}
