package com.example.erp.attendance.web;

import com.example.erp.attendance.domain.AttendanceStatus;
import com.example.erp.attendance.service.AttendanceConflictException;
import com.example.erp.attendance.service.AttendanceService;
import com.example.erp.attendance.web.dto.AttendanceRecordResponse;
import com.example.erp.attendance.web.dto.CheckInRequest;
import com.example.erp.attendance.web.dto.CheckOutRequest;
import com.example.erp.attendance.web.dto.MonthlyAttendanceSummaryResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AttendanceController.class)
@Import(AttendanceExceptionHandler.class)
class AttendanceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AttendanceService attendanceService;

    @Test
    void firstCheckInReturnsCreatedApiResponse() throws Exception {
        UUID employeeId = UUID.randomUUID();
        OffsetDateTime checkInAt = OffsetDateTime.of(2026, 6, 1, 9, 0, 0, 0, ZoneOffset.UTC);
        AttendanceRecordResponse response = new AttendanceRecordResponse(
                UUID.randomUUID(),
                employeeId,
                LocalDate.of(2026, 6, 1),
                checkInAt,
                null,
                List.of(AttendanceStatus.PRESENT),
                0
        );
        when(attendanceService.checkIn(any(CheckInRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/attendance/check-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CheckInRequest(employeeId, checkInAt))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.data.employeeId").value(employeeId.toString()))
                .andExpect(jsonPath("$.data.statuses[0]").value("PRESENT"));
    }

    @Test
    void duplicateCheckInReturnsConflictErrorResponse() throws Exception {
        UUID employeeId = UUID.randomUUID();
        OffsetDateTime checkInAt = OffsetDateTime.of(2026, 6, 1, 9, 0, 0, 0, ZoneOffset.UTC);
        when(attendanceService.checkIn(any(CheckInRequest.class))).thenThrow(new AttendanceConflictException(
                "DUPLICATE_CHECK_IN",
                "Employee already checked in for this date."
        ));

        mockMvc.perform(post("/api/attendance/check-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CheckInRequest(employeeId, checkInAt))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorCode").value("DUPLICATE_CHECK_IN"))
                .andExpect(jsonPath("$.path").value("/api/attendance/check-in"));
    }

    @Test
    void checkOutWithoutCheckInReturnsConflictErrorResponse() throws Exception {
        UUID employeeId = UUID.randomUUID();
        OffsetDateTime checkOutAt = OffsetDateTime.of(2026, 6, 1, 16, 30, 0, 0, ZoneOffset.UTC);
        when(attendanceService.checkOut(any(CheckOutRequest.class))).thenThrow(new AttendanceConflictException(
                "CHECK_IN_NOT_FOUND",
                "Employee must check in before checking out."
        ));

        mockMvc.perform(post("/api/attendance/check-out")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CheckOutRequest(employeeId, checkOutAt))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorCode").value("CHECK_IN_NOT_FOUND"))
                .andExpect(jsonPath("$.path").value("/api/attendance/check-out"));
    }

    @Test
    void monthlySummaryReturnsAggregatedApiResponse() throws Exception {
        UUID employeeId = UUID.randomUUID();
        when(attendanceService.monthlySummary(eq(employeeId), eq(2026), eq(6))).thenReturn(
                new MonthlyAttendanceSummaryResponse(employeeId, 2026, 6, 3, 1, 1, 27, 1330)
        );

        mockMvc.perform(get("/api/attendance/employees/{employeeId}/monthly-summary", employeeId)
                        .queryParam("year", "2026")
                        .queryParam("month", "6"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.data.presentDays").value(3))
                .andExpect(jsonPath("$.data.lateDays").value(1))
                .andExpect(jsonPath("$.data.earlyLeaveDays").value(1))
                .andExpect(jsonPath("$.data.absentDays").value(27))
                .andExpect(jsonPath("$.data.totalWorkedMinutes").value(1330));
    }
}
