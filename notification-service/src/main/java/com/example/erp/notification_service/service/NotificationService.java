package com.example.erp.notification_service.service;

import com.example.erp.notification_service.domain.Notification;
import com.example.erp.notification_service.domain.NotificationType;
import com.example.erp.notification_service.repository.NotificationRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Transactional(readOnly = true)
    public List<Notification> findNotifications(
            String recipientRole,
            UUID recipientUserId,
            Boolean read,
            NotificationType type
    ) {
        Specification<Notification> specification = (root, query, cb) -> cb.conjunction();

        if (recipientRole != null) {
            specification = specification.and((root, query, cb) -> cb.equal(root.get("recipientRole"), recipientRole));
        }
        if (recipientUserId != null) {
            specification = specification.and(
                    (root, query, cb) -> cb.equal(root.get("recipientUserId"), recipientUserId));
        }
        if (read != null) {
            specification = specification.and((root, query, cb) -> cb.equal(root.get("read"), read));
        }
        if (type != null) {
            specification = specification.and((root, query, cb) -> cb.equal(root.get("type"), type));
        }

        return notificationRepository.findAll(specification).stream()
                .sorted(Comparator.comparing(Notification::getCreatedAt).reversed())
                .toList();
    }

    @Transactional
    public Notification markRead(UUID notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotificationNotFoundException(notificationId));
        notification.markRead(OffsetDateTime.now());
        return notificationRepository.save(notification);
    }
}
