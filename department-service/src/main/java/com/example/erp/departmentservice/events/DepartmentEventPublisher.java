package com.example.erp.departmentservice.events;

public interface DepartmentEventPublisher
{
    void publish(DomainEvent<?> event);
}
