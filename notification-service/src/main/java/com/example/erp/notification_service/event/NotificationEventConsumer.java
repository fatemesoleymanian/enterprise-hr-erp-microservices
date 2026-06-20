package com.example.erp.notification_service.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationEventConsumer {

    private final ObjectMapper objectMapper;
    private final NotificationEventService notificationEventService;

    public NotificationEventConsumer(
            ObjectMapper objectMapper,
            NotificationEventService notificationEventService
    ) {
        this.objectMapper = objectMapper;
        this.notificationEventService = notificationEventService;
    }

    @KafkaListener(topics = {
            "${notification.kafka.topics.attendance}",
            "${notification.kafka.topics.employee}"
    })
    public void consume(String message) throws JsonProcessingException {
        notificationEventService.process(objectMapper.readValue(message, NotificationDomainEvent.class));
    }
}
