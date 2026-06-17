package com.example.erp.departmentservice.exceptions;

public class DepartmentDuplicateNameCustomException extends RuntimeException
{
    public DepartmentDuplicateNameCustomException(String name)
    {
        super("Department with name '" + name + "' already exists");
    }
}
