package com.example.erp.notification_service.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    private UUID id;

    @Column(name = "recipient_role", nullable = false, length = 80)
    private String recipientRole;

    @Column(name = "recipient_user_id")
    private UUID recipientUserId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 80)
    private NotificationType type;

    @Column(name = "title", nullable = false, length = 180)
    private String title;

    @Column(name = "body", nullable = false, length = 1000)
    private String body;

    @Column(name = "read", nullable = false)
    private boolean read;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "read_at")
    private OffsetDateTime readAt;

    @Version
    private long version;

    protected Notification() {
    }

    public Notification(
            String recipientRole,
            UUID recipientUserId,
            NotificationType type,
            String title,
            String body,
            OffsetDateTime createdAt
    ) {
        this.id = UUID.randomUUID();
        this.recipientRole = recipientRole;
        this.recipientUserId = recipientUserId;
        this.type = type;
        this.title = title;
        this.body = body;
        this.createdAt = createdAt;
    }

    @PrePersist
    void prePersist() {
        if (id == null) {
            id = UUID.randomUUID();
        }
    }

    public void markRead(OffsetDateTime readAt) {
        if (!read) {
            this.read = true;
            this.readAt = readAt;
        }
    }

    public UUID getId() {
        return id;
    }

    public String getRecipientRole() {
        return recipientRole;
    }

    public UUID getRecipientUserId() {
        return recipientUserId;
    }

    public NotificationType getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public boolean isRead() {
        return read;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getReadAt() {
        return readAt;
    }
}
