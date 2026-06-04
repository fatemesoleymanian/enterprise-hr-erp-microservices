package com.example.erp.attendance.web;

import com.example.erp.attendance.service.AttendanceService;
import com.example.erp.attendance.web.dto.AttendanceRecordResponse;
import com.example.erp.attendance.web.dto.CheckInRequest;
import com.example.erp.attendance.web.dto.CheckOutRequest;
import com.example.erp.attendance.web.dto.MonthlyAttendanceSummaryResponse;
import com.example.erp.common.api.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Validated
@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @PostMapping("/check-in")
    public ResponseEntity<ApiResponse<AttendanceRecordResponse>> checkIn(@Valid @RequestBody CheckInRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(attendanceService.checkIn(request)));
    }

    @PostMapping("/check-out")
    public ApiResponse<AttendanceRecordResponse> checkOut(@Valid @RequestBody CheckOutRequest request) {
        return ApiResponse.success(attendanceService.checkOut(request));
    }

    @GetMapping("/employees/{employeeId}")
    public ApiResponse<List<AttendanceRecordResponse>> employeeAttendance(
            @PathVariable UUID employeeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        return ApiResponse.success(attendanceService.findEmployeeAttendance(employeeId, from, to));
    }

    @GetMapping("/employees/{employeeId}/monthly-summary")
    public ApiResponse<MonthlyAttendanceSummaryResponse> monthlySummary(
            @PathVariable UUID employeeId,
            @RequestParam @Min(2000) @Max(2100) int year,
            @RequestParam @Min(1) @Max(12) int month
    ) {
        return ApiResponse.success(attendanceService.monthlySummary(employeeId, year, month));
    }
}
