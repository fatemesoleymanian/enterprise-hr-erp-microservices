package com.example.erp.departmentservice.mapper;

import com.example.erp.departmentservice.domain.Department;
import com.example.erp.departmentservice.dto.CreateDepartmentRequestDto;
import com.example.erp.departmentservice.dto.CreateDepartmentResponseDto;

import java.time.OffsetDateTime;

public class CreateDepartmentMapper
{
    public static Department mapCreateToEntity(CreateDepartmentRequestDto dto)
    {
        var entity = new Department();
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());

        return entity;
    }

    public static CreateDepartmentResponseDto mapEntityToCreate(Department entity)
    {
        var dto = new CreateDepartmentResponseDto();
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setCreatedAt(OffsetDateTime.now());
        dto.setUpdatedAt(OffsetDateTime.now());
        dto.setId(entity.getId());

        return dto;
    }


}
