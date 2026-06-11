package com.example.erp.departmentservice.events;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaDepartmentEventPublisher implements DepartmentEventPublisher{
    private final KafkaTemplate<Object, Object> kafkaTemplate;
    private final String topic;

    public KafkaDepartmentEventPublisher(
            KafkaTemplate<Object, Object> kafkaTemplate,
            @Value("${department.kafka.topic}") String topic
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }
    @Override
    public void publish(DomainEvent<?> event)
    {
        kafkaTemplate.send(topic, event.eventId().toString(), event);
    }
}
