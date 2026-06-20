package com.example.erp.employeeservice.service.contracts;

import java.util.UUID;

public interface IUserClientService
{
    boolean isUserExists(UUID userId);
}
