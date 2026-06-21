package com.example.erp.notification_service.event;

import com.example.erp.notification_service.domain.Notification;
import com.example.erp.notification_service.domain.NotificationType;
import com.example.erp.notification_service.domain.ProcessedNotificationEvent;
import com.example.erp.notification_service.repository.NotificationRepository;
import com.example.erp.notification_service.repository.ProcessedNotificationEventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
public class NotificationEventService {

    private static final String HR_ROLE = "HR";

    private final ObjectMapper objectMapper;
    private final NotificationRepository notificationRepository;
    private final ProcessedNotificationEventRepository processedEventRepository;

    public NotificationEventService(
            ObjectMapper objectMapper,
            NotificationRepository notificationRepository,
            ProcessedNotificationEventRepository processedEventRepository
    ) {
        this.objectMapper = objectMapper;
        this.notificationRepository = notificationRepository;
        this.processedEventRepository = processedEventRepository;
    }

    @Transactional
    public void process(NotificationDomainEvent event) {
        if (processedEventRepository.existsById(event.eventId())) {
            return;
        }

        switch (event.eventType()) {
            case "AttendanceViolationDetected" -> createAttendanceViolationNotification(
                    payload(event, AttendanceViolationDetectedPayload.class), event.occurredAt());
            case "EmployeeStatusChanged" -> createEmployeeStatusChangedNotification(
                    payload(event, EmployeeStatusChangedPayload.class), event.occurredAt());
            default -> {
                // Other events may share these topics and do not create MVP notifications.
            }
        }

        processedEventRepository.save(new ProcessedNotificationEvent(
                event.eventId(),
                event.eventType(),
                OffsetDateTime.now()
        ));
    }

    private void createAttendanceViolationNotification(
            AttendanceViolationDetectedPayload payload,
            OffsetDateTime eventTime
    ) {
        notificationRepository.save(new Notification(
                HR_ROLE,
                null,
                NotificationType.ATTENDANCE_VIOLATION,
                "Attendance violation detected",
                "Employee " + payload.employeeId() + " had attendance violation "
                        + payload.violationType() + " on " + payload.attendanceDate() + ".",
                timestamp(payload.detectedAt(), eventTime)
        ));
    }

    private void createEmployeeStatusChangedNotification(
            EmployeeStatusChangedPayload payload,
            OffsetDateTime eventTime
    ) {
        notificationRepository.save(new Notification(
                HR_ROLE,
                null,
                NotificationType.EMPLOYEE_STATUS_CHANGED,
                "Employee status changed",
                "Employee " + payload.employeeId() + " status changed to " + payload.status() + ".",
                timestamp(payload.changedAt(), eventTime)
        ));
    }

    private <T> T payload(NotificationDomainEvent event, Class<T> payloadType) {
        return objectMapper.convertValue(event.payload(), payloadType);
    }

    private static OffsetDateTime timestamp(OffsetDateTime preferred, OffsetDateTime fallback) {
        return preferred == null ? fallback : preferred;
    }
}
