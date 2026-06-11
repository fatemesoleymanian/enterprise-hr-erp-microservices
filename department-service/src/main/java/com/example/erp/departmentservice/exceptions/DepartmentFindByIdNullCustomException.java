package com.example.erp.departmentservice.exceptions;

import java.util.UUID;

public class DepartmentFindByIdNullCustomException extends RuntimeException
{
    public DepartmentFindByIdNullCustomException(UUID id) {
        super("Department with id : " + id + "does not exist!");
    }
}
