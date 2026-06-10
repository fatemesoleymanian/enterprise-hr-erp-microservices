package com.example.erp.departmentservice.domain;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "departments")
public class DepartmentRecord
{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(name = "name",length = 255, nullable = false, unique = true)
    private String name;
    @Column(name = "description",nullable = true)
    private String description;
    @Column(name = "parent_department_id",nullable = true)
    private UUID parentDepartmentId;
    @Column(name = "manager_user_id",nullable = false)
    private UUID managerUserId;
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    public DepartmentRecord(UUID id, String name, String description, UUID parentDepartmentId,
                            UUID managerUserId, OffsetDateTime createdAt, OffsetDateTime updatedAt,
                            Long version) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.parentDepartmentId = parentDepartmentId;
        this.managerUserId = managerUserId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.version = version;
    }

    public DepartmentRecord() {
    }

    public UUID getId() {
        return id;
    }

    public Long getVersion() {
        return version;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public UUID getParentDepartmentId() {
        return parentDepartmentId;
    }

    public UUID getManagerUserId() {
        return managerUserId;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setManagerUserId(UUID managerUserId) {
        this.managerUserId = managerUserId;
    }

    public void setParentDepartmentId(UUID parentDepartmentId) {
        this.parentDepartmentId = parentDepartmentId;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setName(String name) {
        this.name = name;
    }
}

