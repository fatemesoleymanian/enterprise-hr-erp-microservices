package com.example.erp.reporting_service.projection;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "employee_report_views")
public class EmployeeReportView {

    @Id
    @Column(name = "employee_id")
    private UUID employeeId;

    @Column(name = "department_id")
    private UUID departmentId;

    @Column(name = "status", nullable = false, length = 30)
    private String status;

    @Column(name = "job_title", length = 150)
    private String jobTitle;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    protected EmployeeReportView() {
    }

    public EmployeeReportView(
            UUID employeeId,
            UUID departmentId,
            String status,
            String jobTitle,
            OffsetDateTime updatedAt) {
        this.employeeId = employeeId;
        this.departmentId = departmentId;
        this.status = status;
        this.jobTitle = jobTitle;
        this.updatedAt = updatedAt;
    }

    public UUID getEmployeeId() {
        return employeeId;
    }

    public UUID getDepartmentId() {
        return departmentId;
    }

    public String getStatus() {
        return status;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setEmployeeId(UUID employeeId) {
        this.employeeId = employeeId;
    }

    public void setDepartmentId(UUID departmentId) {
        this.departmentId = departmentId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
