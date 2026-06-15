package com.example.erp.departmentservice.service.Contracts;

import com.example.erp.departmentservice.dto.*;
import jakarta.validation.constraints.NotBlank;

import java.util.List;
import java.util.UUID;

public interface IDepartmentService
{
    CreateDepartmentResponseDto create(CreateDepartmentRequestDto dto);
    UpdateDepartmentResponseDto update(UpdateDepartmentRequestDto dto);
    AssignDepartmentUpdateResponseDto assignDepartmentToManager(@NotBlank UUID id, @NotBlank     UUID managerId);
    boolean existByName(String name);
    FindDepartmentResponseDto findById(UUID id);
    List<FindDepartmentResponseDto> findAll();

}
