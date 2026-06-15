package com.example.erp.departmentservice.service.Contracts;

import java.util.UUID;

public interface IUserClientService
{
    boolean isUserExists(UUID userId);
    boolean UpdateUserRole(UUID userId);
}
