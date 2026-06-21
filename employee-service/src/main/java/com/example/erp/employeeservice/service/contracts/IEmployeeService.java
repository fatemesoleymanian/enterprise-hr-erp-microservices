package com.example.erp.employeeservice.service.contracts;

import com.example.erp.employeeservice.domain.Status;
import com.example.erp.employeeservice.dto.*;

import java.util.ArrayList;
import java.util.UUID;

public interface IEmployeeService
{
    CreateEmployeeResponseDto create(CreateEmployeeRequestDto createEmployeeRequestDto);
    FindEmployeeResponseDto findById(UUID id);
    ArrayList<FindEmployeeResponseDto> findAll();
    UpdateEmployeeResponseDto update(UUID id, UpdateEmployeeRequestDto updateEmployeeRequestDto);
    UpdateStatusEmployeeResponseDto updateStatus(UUID id, Status status);
    UpdateDepartmentEmployeeResponseDto updateDepartment(UUID id, UUID department_id);


}
