package com.example.erp.notification_service.web.dto;

import com.example.erp.notification_service.domain.Notification;
import com.example.erp.notification_service.domain.NotificationType;

import java.time.OffsetDateTime;
import java.util.UUID;

public record NotificationResponse(
        UUID id,
        String recipientRole,
        UUID recipientUserId,
        NotificationType type,
        String title,
        String body,
        boolean read,
        OffsetDateTime createdAt,
        OffsetDateTime readAt
) {
    public static NotificationResponse from(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getRecipientRole(),
                notification.getRecipientUserId(),
                notification.getType(),
                notification.getTitle(),
                notification.getBody(),
                notification.isRead(),
                notification.getCreatedAt(),
                notification.getReadAt()
        );
    }
}
