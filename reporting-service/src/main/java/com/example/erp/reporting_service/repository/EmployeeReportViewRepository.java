package com.example.erp.reporting_service.repository;

import com.example.erp.reporting_service.projection.EmployeeReportView;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EmployeeReportViewRepository extends JpaRepository<EmployeeReportView, UUID> {

    List<EmployeeReportView> findByDepartmentId(UUID departmentId);

    long countByDepartmentIdAndStatus(UUID departmentId, String status);

    long countByStatus(String status);
}
