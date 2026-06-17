package com.example.erp.departmentservice.events;

public interface IDepartmentEventPublisher
{
    void publish(DomainEvent<?> event);
}
