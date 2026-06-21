package com.example.erp.notification_service.service;

import com.example.erp.notification_service.domain.Notification;
import com.example.erp.notification_service.domain.NotificationType;
import com.example.erp.notification_service.repository.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Test
    void markNotificationReadSetsReadTrueAndReadAt() {
        UUID notificationId = UUID.randomUUID();
        Notification notification = new Notification(
                "HR",
                null,
                NotificationType.ATTENDANCE_VIOLATION,
                "Attendance violation detected",
                "Employee was late.",
                OffsetDateTime.now()
        );
        when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(notification));
        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> invocation.getArgument(0));
        NotificationService service = new NotificationService(notificationRepository);

        Notification result = service.markRead(notificationId);

        assertThat(result.isRead()).isTrue();
        assertThat(result.getReadAt()).isNotNull();
    }
}
