package com.example.erp.employeeservice.events;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;

public class KafkaEmployeeEventPublisher implements IEmployeeEventPublisher
{
    private final KafkaTemplate<Object, Object> kafkaTemplate;
    private final String topic;

    public KafkaEmployeeEventPublisher(KafkaTemplate<Object, Object> kafkaTemplate,
                                       @Value("${employee.kafka.topic}") String topic  )
    {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    @Override
    public void publish(DomainEvent<?> event)
    {
        kafkaTemplate.send(topic, event.eventId().toString(), event);

    }
}
