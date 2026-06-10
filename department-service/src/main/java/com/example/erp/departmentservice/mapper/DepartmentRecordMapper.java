package com.example.erp.departmentservice.mapper;

import com.example.erp.departmentservice.domain.DepartmentRecord;
import com.example.erp.departmentservice.dto.CreateRequestRecordDto;
import com.example.erp.departmentservice.dto.CreateResponseRecordDto;

import java.time.OffsetDateTime;

public class DepartmentRecordMapper
{
    public static DepartmentRecord mapCreateToEntity(CreateRequestRecordDto dto)
    {
        var entity = new DepartmentRecord();
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());

        return entity;
    }

    public static CreateResponseRecordDto mapEntityToCreate(DepartmentRecord entity)
    {
        var dto = new CreateResponseRecordDto();
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setCreatedAt(OffsetDateTime.now());
        dto.setUpdatedAt(OffsetDateTime.now());
        dto.setId(entity.getId());

        return dto;
    }


}
