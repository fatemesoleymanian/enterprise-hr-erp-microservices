package com.example.erp.employeeservice.events;

import org.springframework.stereotype.Component;

@Component
public interface IEmployeeEventPublisher
{
    void publish(DomainEvent<?> event);
}
