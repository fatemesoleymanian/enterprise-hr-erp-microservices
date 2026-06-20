package com.example.erp.notification_service.repository;

import com.example.erp.notification_service.domain.ProcessedNotificationEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProcessedNotificationEventRepository extends JpaRepository<ProcessedNotificationEvent, UUID> {
}
