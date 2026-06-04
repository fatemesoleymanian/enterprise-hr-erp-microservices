package com.example.erp.attendance.service;

import com.example.erp.attendance.web.dto.AttendanceRecordResponse;
import com.example.erp.attendance.web.dto.CheckInRequest;
import com.example.erp.attendance.web.dto.CheckOutRequest;
import com.example.erp.attendance.web.dto.MonthlyAttendanceSummaryResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface AttendanceService {

    AttendanceRecordResponse checkIn(CheckInRequest request);

    AttendanceRecordResponse checkOut(CheckOutRequest request);

    List<AttendanceRecordResponse> findEmployeeAttendance(UUID employeeId, LocalDate from, LocalDate to);

    MonthlyAttendanceSummaryResponse monthlySummary(UUID employeeId, int year, int month);
}
