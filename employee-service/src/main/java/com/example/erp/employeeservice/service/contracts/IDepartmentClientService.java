package com.example.erp.employeeservice.service.contracts;

import java.util.UUID;

public interface IDepartmentClientService
{
    boolean isDepartmentExists(UUID department_id);
}
