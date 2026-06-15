package com.example.erp.reporting_service.projection;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "attendance_report_contributions")
public class AttendanceReportContribution {

    @Id
    @Column(name = "attendance_record_id")
    private UUID attendanceRecordId;

    @Column(name = "employee_id", nullable = false)
    private UUID employeeId;

    @Column(name = "attendance_date", nullable = false)
    private LocalDate attendanceDate;

    @Column(name = "present", nullable = false)
    private boolean present;

    @Column(name = "late", nullable = false)
    private boolean late;

    @Column(name = "early_leave", nullable = false)
    private boolean earlyLeave;

    @Column(name = "absent", nullable = false)
    private boolean absent;

    @Column(name = "worked_minutes", nullable = false)
    private int workedMinutes;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    protected AttendanceReportContribution() {
    }

    public AttendanceReportContribution(
            UUID attendanceRecordId,
            UUID employeeId,
            LocalDate attendanceDate,
            OffsetDateTime updatedAt
    ) {
        this.attendanceRecordId = attendanceRecordId;
        this.employeeId = employeeId;
        this.attendanceDate = attendanceDate;
        this.updatedAt = updatedAt;
    }

    public UUID getAttendanceRecordId() {
        return attendanceRecordId;
    }

    public UUID getEmployeeId() {
        return employeeId;
    }

    public LocalDate getAttendanceDate() {
        return attendanceDate;
    }

    public boolean isPresent() {
        return present;
    }

    public boolean isLate() {
        return late;
    }

    public boolean isEarlyLeave() {
        return earlyLeave;
    }

    public boolean isAbsent() {
        return absent;
    }

    public int getWorkedMinutes() {
        return workedMinutes;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setEmployeeId(UUID employeeId) {
        this.employeeId = employeeId;
    }

    public void setAttendanceDate(LocalDate attendanceDate) {
        this.attendanceDate = attendanceDate;
    }

    public void setPresent(boolean present) {
        this.present = present;
    }

    public void setLate(boolean late) {
        this.late = late;
    }

    public void setEarlyLeave(boolean earlyLeave) {
        this.earlyLeave = earlyLeave;
    }

    public void setAbsent(boolean absent) {
        this.absent = absent;
    }

    public void setWorkedMinutes(int workedMinutes) {
        this.workedMinutes = workedMinutes;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
