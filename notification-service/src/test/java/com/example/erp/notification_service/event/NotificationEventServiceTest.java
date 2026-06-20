package com.example.erp.notification_service.event;

import com.example.erp.notification_service.domain.Notification;
import com.example.erp.notification_service.domain.NotificationType;
import com.example.erp.notification_service.repository.NotificationRepository;
import com.example.erp.notification_service.repository.ProcessedNotificationEventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationEventServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private ProcessedNotificationEventRepository processedEventRepository;

    private ObjectMapper objectMapper;
    private NotificationEventService notificationEventService;

    @BeforeEach
    void setUp() {
        objectMapper = JsonMapper.builder().addModule(new JavaTimeModule()).build();
        notificationEventService = new NotificationEventService(
                objectMapper,
                notificationRepository,
                processedEventRepository
        );
    }

    @Test
    void attendanceViolationDetectedCreatesUnreadHrNotification() {
        UUID employeeId = UUID.randomUUID();
        NotificationDomainEvent event = event("AttendanceViolationDetected", new AttendanceViolationDetectedPayload(
                UUID.randomUUID(),
                employeeId,
                LocalDate.of(2026, 6, 20),
                "LATE",
                time(2026, 6, 20, 9, 15)
        ));

        notificationEventService.process(event);

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(captor.capture());
        assertThat(captor.getValue().getRecipientRole()).isEqualTo("HR");
        assertThat(captor.getValue().getType()).isEqualTo(NotificationType.ATTENDANCE_VIOLATION);
        assertThat(captor.getValue().getBody()).contains(employeeId.toString(), "LATE");
        assertThat(captor.getValue().isRead()).isFalse();
    }

    @Test
    void employeeStatusChangedCreatesUnreadHrNotification() {
        UUID employeeId = UUID.randomUUID();
        NotificationDomainEvent event = event("EmployeeStatusChanged", new EmployeeStatusChangedPayload(
                employeeId,
                "SUSPENDED",
                time(2026, 6, 20, 10, 0)
        ));

        notificationEventService.process(event);

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(captor.capture());
        assertThat(captor.getValue().getRecipientRole()).isEqualTo("HR");
        assertThat(captor.getValue().getType()).isEqualTo(NotificationType.EMPLOYEE_STATUS_CHANGED);
        assertThat(captor.getValue().getBody()).contains(employeeId.toString(), "SUSPENDED");
        assertThat(captor.getValue().isRead()).isFalse();
    }

    @Test
    void duplicateEventDoesNotCreateDuplicateNotification() {
        NotificationDomainEvent event = event("EmployeeStatusChanged", new EmployeeStatusChangedPayload(
                UUID.randomUUID(),
                "TERMINATED",
                time(2026, 6, 20, 10, 0)
        ));
        when(processedEventRepository.existsById(event.eventId())).thenReturn(true);

        notificationEventService.process(event);

        verify(notificationRepository, never()).save(any());
        verify(processedEventRepository, never()).save(any());
    }

    private NotificationDomainEvent event(String eventType, Object payload) {
        return new NotificationDomainEvent(
                UUID.randomUUID(),
                eventType,
                time(2026, 6, 20, 10, 0),
                1,
                "test-service",
                UUID.randomUUID(),
                objectMapper.valueToTree(payload)
        );
    }

    private static OffsetDateTime time(int year, int month, int day, int hour, int minute) {
        return OffsetDateTime.of(year, month, day, hour, minute, 0, 0, ZoneOffset.UTC);
    }
}
