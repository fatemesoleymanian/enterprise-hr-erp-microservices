package com.example.erp.departmentservice.mapper;

import com.example.erp.departmentservice.domain.Department;
import com.example.erp.departmentservice.dto.FindDepartmentResponseDto;

public class FindDepartmentMapper
{
    public static FindDepartmentResponseDto mapEntityToFind(Department entity)
    {
        var dto = new FindDepartmentResponseDto();
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setId(entity.getId());

        return dto;
    }
}
