package com.example.erp.attendance.event;

import java.time.OffsetDateTime;
import java.util.UUID;

public record DomainEvent<T>(
        UUID eventId,
        String eventType,
        OffsetDateTime occurredAt,
        int version,
        String producer,
        UUID correlationId,
        T payload
) {
    private static final int CURRENT_VERSION = 1;
    private static final String PRODUCER = "attendance-service";

    public static <T> DomainEvent<T> attendanceEvent(String eventType, T payload) {
        return new DomainEvent<>(
                UUID.randomUUID(),
                eventType,
                OffsetDateTime.now(),
                CURRENT_VERSION,
                PRODUCER,
                UUID.randomUUID(),
                payload
        );
    }
}
