package com.example.erp.attendance.event;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaAttendanceEventPublisher implements AttendanceEventPublisher {

    private final KafkaTemplate<Object, Object> kafkaTemplate;
    private final String topic;

    public KafkaAttendanceEventPublisher(
            KafkaTemplate<Object, Object> kafkaTemplate,
            @Value("${attendance.kafka.topic}") String topic
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    @Override
    public void publish(DomainEvent<?> event) {
        kafkaTemplate.send(topic, event.eventId().toString(), event);
    }
}
