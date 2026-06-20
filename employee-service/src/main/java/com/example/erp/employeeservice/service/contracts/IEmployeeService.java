package com.example.erp.employeeservice.service.contracts;

import com.example.erp.employeeservice.dto.CreateEmployeeRequestDto;
import com.example.erp.employeeservice.dto.CreateEmployeeResponseDto;

public interface IEmployeeService
{
    CreateEmployeeResponseDto create(CreateEmployeeRequestDto createEmployeeRequestDto);

}
