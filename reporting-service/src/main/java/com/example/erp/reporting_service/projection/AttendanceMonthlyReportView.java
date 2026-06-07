package com.example.erp.reporting_service.projection;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "attendance_monthly_report_views",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_attendance_monthly_employee_month",
                columnNames = {"employee_id", "report_year", "report_month"}
        )
)
public class AttendanceMonthlyReportView {

    @Id
    private UUID id;

    @Column(name = "employee_id", nullable = false)
    private UUID employeeId;

    @Column(name = "report_year", nullable = false)
    private int reportYear;

    @Column(name = "report_month", nullable = false)
    private int reportMonth;

    @Column(name = "present_days", nullable = false)
    private int presentDays;

    @Column(name = "late_days", nullable = false)
    private int lateDays;

    @Column(name = "early_leave_days", nullable = false)
    private int earlyLeaveDays;

    @Column(name = "absent_days", nullable = false)
    private int absentDays;

    @Column(name = "total_worked_minutes", nullable = false)
    private int totalWorkedMinutes;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    protected AttendanceMonthlyReportView() {
    }

    public AttendanceMonthlyReportView(
            UUID employeeId,
            int reportYear,
            int reportMonth,
            int presentDays,
            int lateDays,
            int earlyLeaveDays,
            int absentDays,
            int totalWorkedMinutes,
            OffsetDateTime updatedAt
    ) {
        this.id = UUID.randomUUID();
        this.employeeId = employeeId;
        this.reportYear = reportYear;
        this.reportMonth = reportMonth;
        this.presentDays = presentDays;
        this.lateDays = lateDays;
        this.earlyLeaveDays = earlyLeaveDays;
        this.absentDays = absentDays;
        this.totalWorkedMinutes = totalWorkedMinutes;
        this.updatedAt = updatedAt;
    }

    @PrePersist
    void prePersist() {
        if (id == null) {
            id = UUID.randomUUID();
        }
    }

    public UUID getId() {
        return id;
    }

    public UUID getEmployeeId() {
        return employeeId;
    }

    public int getReportYear() {
        return reportYear;
    }

    public int getReportMonth() {
        return reportMonth;
    }

    public int getPresentDays() {
        return presentDays;
    }

    public int getLateDays() {
        return lateDays;
    }

    public int getEarlyLeaveDays() {
        return earlyLeaveDays;
    }

    public int getAbsentDays() {
        return absentDays;
    }

    public int getTotalWorkedMinutes() {
        return totalWorkedMinutes;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setEmployeeId(UUID employeeId) {
        this.employeeId = employeeId;
    }

    public void setReportYear(int reportYear) {
        this.reportYear = reportYear;
    }

    public void setReportMonth(int reportMonth) {
        this.reportMonth = reportMonth;
    }

    public void setPresentDays(int presentDays) {
        this.presentDays = presentDays;
    }

    public void setLateDays(int lateDays) {
        this.lateDays = lateDays;
    }

    public void setEarlyLeaveDays(int earlyLeaveDays) {
        this.earlyLeaveDays = earlyLeaveDays;
    }

    public void setAbsentDays(int absentDays) {
        this.absentDays = absentDays;
    }

    public void setTotalWorkedMinutes(int totalWorkedMinutes) {
        this.totalWorkedMinutes = totalWorkedMinutes;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
