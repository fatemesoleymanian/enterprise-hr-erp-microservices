package com.example.erp.reporting_service.repository;

import com.example.erp.reporting_service.projection.DepartmentReportView;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DepartmentReportViewRepository extends JpaRepository<DepartmentReportView, UUID> {
}
