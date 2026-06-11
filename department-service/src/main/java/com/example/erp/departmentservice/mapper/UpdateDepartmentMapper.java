package com.example.erp.departmentservice.mapper;

import com.example.erp.departmentservice.domain.Department;
import com.example.erp.departmentservice.dto.UpdateDepartmentRequestDto;
import com.example.erp.departmentservice.dto.UpdateDepartmentResponseDto;

import java.time.OffsetDateTime;

public class UpdateDepartmentMapper
{
    public static Department mapUpdateToEntity(UpdateDepartmentRequestDto dto)
    {
        var entity = new Department();
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());

        return entity;
    }

    public static UpdateDepartmentResponseDto mapEntityToUpdate(Department entity)
    {
        var dto = new UpdateDepartmentResponseDto();
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(OffsetDateTime.now());
        dto.setId(entity.getId());

        return dto;
    }
}
