package com.example.erp.reporting_service.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ReportingEventConsumer {

    private final ObjectMapper objectMapper;
    private final ReportingProjectionService projectionService;

    public ReportingEventConsumer(ObjectMapper objectMapper, ReportingProjectionService projectionService) {
        this.objectMapper = objectMapper;
        this.projectionService = projectionService;
    }

    @KafkaListener(topics = {
            "${reporting.kafka.topics.department}",
            "${reporting.kafka.topics.employee}",
            "${reporting.kafka.topics.attendance}"
    })
    public void consume(String message) throws JsonProcessingException {
        projectionService.process(objectMapper.readValue(message, ReportingDomainEvent.class));
    }
}
