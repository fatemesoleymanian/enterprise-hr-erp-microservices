package com.example.erp.attendance.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "attendance_records")
public class AttendanceRecord {

    @Id
    private UUID id;

    @Column(name = "employee_id", nullable = false)
    private UUID employeeId;

    @Column(name = "attendance_date", nullable = false)
    private LocalDate attendanceDate;

    @Column(name = "check_in_at")
    private OffsetDateTime checkInAt;

    @Column(name = "check_out_at")
    private OffsetDateTime checkOutAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 40)
    private AttendanceStatus status;

    @Column(name = "worked_minutes", nullable = false)
    private int workedMinutes;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    protected AttendanceRecord() {
    }

    public AttendanceRecord(UUID employeeId, LocalDate attendanceDate, OffsetDateTime checkInAt, AttendanceStatus status) {
        this.id = UUID.randomUUID();
        this.employeeId = employeeId;
        this.attendanceDate = attendanceDate;
        this.checkInAt = checkInAt;
        this.status = status;
        this.workedMinutes = 0;
    }

    @PrePersist
    void prePersist() {
        OffsetDateTime now = OffsetDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = OffsetDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public UUID getEmployeeId() {
        return employeeId;
    }

    public LocalDate getAttendanceDate() {
        return attendanceDate;
    }

    public OffsetDateTime getCheckInAt() {
        return checkInAt;
    }

    public OffsetDateTime getCheckOutAt() {
        return checkOutAt;
    }

    public AttendanceStatus getStatus() {
        return status;
    }

    public int getWorkedMinutes() {
        return workedMinutes;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public Long getVersion() {
        return version;
    }

    public void checkOut(OffsetDateTime checkOutAt, AttendanceStatus status, int workedMinutes) {
        this.checkOutAt = checkOutAt;
        this.status = status;
        this.workedMinutes = workedMinutes;
    }
}
