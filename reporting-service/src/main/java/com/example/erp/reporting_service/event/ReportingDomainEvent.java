package com.example.erp.reporting_service.event;

import com.fasterxml.jackson.databind.JsonNode;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ReportingDomainEvent(
        UUID eventId,
        String eventType,
        OffsetDateTime occurredAt,
        int version,
        String producer,
        UUID correlationId,
        JsonNode payload
) {
}
