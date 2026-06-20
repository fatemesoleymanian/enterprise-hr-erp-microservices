package com.example.erp.employeeservice.service.contracts;

import com.example.erp.employeeservice.dto.CreateEmployeeRequestDto;
import com.example.erp.employeeservice.dto.CreateEmployeeResponseDto;
import com.example.erp.employeeservice.dto.FindEmployeeResponseDto;

import java.util.ArrayList;
import java.util.UUID;

public interface IEmployeeService
{
    CreateEmployeeResponseDto create(CreateEmployeeRequestDto createEmployeeRequestDto);
    FindEmployeeResponseDto findById(UUID id);
    ArrayList<FindEmployeeResponseDto> findAll();


}
