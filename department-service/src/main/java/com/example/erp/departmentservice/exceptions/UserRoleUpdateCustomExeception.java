package com.example.erp.departmentservice.exceptions;

import java.util.UUID;

public class UserRoleUpdateCustomExeception extends RuntimeException
{
    public UserRoleUpdateCustomExeception(UUID managerId) {
        super("Manager with id:" + managerId + "did not updated");
    }
}
