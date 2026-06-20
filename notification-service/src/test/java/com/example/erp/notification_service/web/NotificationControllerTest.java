package com.example.erp.notification_service.web;

import com.example.erp.notification_service.domain.Notification;
import com.example.erp.notification_service.domain.NotificationType;
import com.example.erp.notification_service.service.NotificationNotFoundException;
import com.example.erp.notification_service.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NotificationController.class)
@Import(NotificationExceptionHandler.class)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NotificationService notificationService;

    @Test
    void listNotificationsReturnsApiResponse() throws Exception {
        Notification notification = new Notification(
                "HR",
                null,
                NotificationType.ATTENDANCE_VIOLATION,
                "Attendance violation detected",
                "Employee was late.",
                OffsetDateTime.of(2026, 6, 20, 9, 15, 0, 0, ZoneOffset.UTC)
        );
        when(notificationService.findNotifications("HR", null, false, NotificationType.ATTENDANCE_VIOLATION))
                .thenReturn(List.of(notification));

        mockMvc.perform(get("/api/notifications")
                        .queryParam("recipientRole", "HR")
                        .queryParam("read", "false")
                        .queryParam("type", "ATTENDANCE_VIOLATION"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.data[0].recipientRole").value("HR"))
                .andExpect(jsonPath("$.data[0].type").value("ATTENDANCE_VIOLATION"))
                .andExpect(jsonPath("$.data[0].read").value(false));
    }

    @Test
    void markReadReturnsReadNotification() throws Exception {
        UUID notificationId = UUID.randomUUID();
        Notification notification = new Notification(
                "HR",
                null,
                NotificationType.EMPLOYEE_STATUS_CHANGED,
                "Employee status changed",
                "Employee status changed to SUSPENDED.",
                OffsetDateTime.now()
        );
        notification.markRead(OffsetDateTime.of(2026, 6, 20, 10, 0, 0, 0, ZoneOffset.UTC));
        when(notificationService.markRead(notificationId)).thenReturn(notification);

        mockMvc.perform(patch("/api/notifications/{id}/read", notificationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.read").value(true))
                .andExpect(jsonPath("$.data.readAt").exists());
    }

    @Test
    void missingNotificationReturnsNotFound() throws Exception {
        UUID notificationId = UUID.randomUUID();
        when(notificationService.markRead(notificationId)).thenThrow(new NotificationNotFoundException(notificationId));

        mockMvc.perform(patch("/api/notifications/{id}/read", notificationId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("NOTIFICATION_NOT_FOUND"))
                .andExpect(jsonPath("$.path").value("/api/notifications/" + notificationId + "/read"));
    }

    @Test
    void invalidTypeReturnsStandardErrorResponse() throws Exception {
        mockMvc.perform(get("/api/notifications")
                        .queryParam("type", "UNKNOWN"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("INVALID_NOTIFICATION_FILTER"));
    }
}
