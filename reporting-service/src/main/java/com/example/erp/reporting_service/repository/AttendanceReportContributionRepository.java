package com.example.erp.reporting_service.repository;

import com.example.erp.reporting_service.projection.AttendanceReportContribution;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AttendanceReportContributionRepository
        extends JpaRepository<AttendanceReportContribution, UUID> {
}
