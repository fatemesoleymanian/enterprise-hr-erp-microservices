package com.example.erp.departmentservice.mapper;

import com.example.erp.departmentservice.domain.Department;
import com.example.erp.departmentservice.dto.AssignDepartmentUpdateResponseDto;

public class AssignDepartmentUpdateMapper
{
    public static AssignDepartmentUpdateResponseDto mapEntityToUpdate(Department entity)
    {
        var dto = new AssignDepartmentUpdateResponseDto();
        dto.setId(entity.getId());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setManagerId(entity.getManagerUserId());

        return dto;
    }

    public static AssignDepartmentUpdateResponseDto mapEntityToEvent(Department entity)
    {
        var event = new AssignDepartmentUpdateResponseDto();
        event.setUpdatedAt(entity.getUpdatedAt());
        event.setId(entity.getId());
        event.setManagerId(entity.getManagerUserId());
        return event;
    }

}
