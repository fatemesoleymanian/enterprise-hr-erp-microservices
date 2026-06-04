package com.example.erp.attendance.event;

public interface AttendanceEventPublisher {

    void publish(DomainEvent<?> event);
}
