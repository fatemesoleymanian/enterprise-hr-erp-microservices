package com.example.erp.reporting_service.projection;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "department_report_views")
public class DepartmentReportView {

    @Id
    @Column(name = "department_id")
    private UUID departmentId;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "manager_user_id")
    private UUID managerUserId;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    protected DepartmentReportView() {
    }

    public DepartmentReportView(UUID departmentId, String name, UUID managerUserId, OffsetDateTime updatedAt) {
        this.departmentId = departmentId;
        this.name = name;
        this.managerUserId = managerUserId;
        this.updatedAt = updatedAt;
    }

    public UUID getDepartmentId() {
        return departmentId;
    }

    public String getName() {
        return name;
    }

    public UUID getManagerUserId() {
        return managerUserId;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setDepartmentId(UUID departmentId) {
        this.departmentId = departmentId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setManagerUserId(UUID managerUserId) {
        this.managerUserId = managerUserId;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
