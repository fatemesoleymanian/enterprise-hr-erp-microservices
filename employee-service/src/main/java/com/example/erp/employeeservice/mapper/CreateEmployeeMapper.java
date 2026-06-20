package com.example.erp.employeeservice.mapper;

import com.example.erp.employeeservice.domain.Employee;
import com.example.erp.employeeservice.dto.CreateEmployeeRequestDto;
import com.example.erp.employeeservice.dto.CreateEmployeeResponseDto;
import com.example.erp.employeeservice.events.EmployeeCreatedPayload;

import java.time.OffsetDateTime;

public class CreateEmployeeMapper
{
    public static Employee mapCreateToEntity(CreateEmployeeRequestDto dto)
    {
        var entity = new Employee();
        entity.setCreated_at(OffsetDateTime.now());
        entity.setEmail(entity.getEmail());
        entity.setDepartment_id(entity.getDepartment_id());
        entity.setEmployee_number(dto.getEmployee_number());
        entity.setFirst_name(dto.getFirst_name());
        entity.setLast_name(entity.getLast_name());
        entity.setHire_date(dto.getHire_date());
        entity.setUser_id(dto.getUser_id());
        entity.setVersion(dto.getVersion());
        entity.setUpdated_at(OffsetDateTime.now());
        entity.setManager_employee_id(dto.getManager_employee_id());

        return entity;
    }
    public static CreateEmployeeResponseDto mapEntityToCreate(Employee entity)
    {
        var dto = new CreateEmployeeResponseDto();
        dto.setCreated_at(entity.getCreated_at());
        dto.setUpdated_at(entity.getUpdated_at());
        dto.setId(entity.getId());
        dto.setEmail(dto.getEmail());
        dto.setStatus_name(entity.getStatus());
        dto.setFirst_name(dto.getFirst_name());
        dto.setLast_name(dto.getLast_name());
        dto.setEmployee_number(dto.getEmployee_number());
        dto.setDepartment_id(dto.getDepartment_id());
        dto.setHire_date(dto.getHire_date());
        dto.setUser_id(dto.getUser_id());
        dto.setManager_employee_id(entity.getManager_employee_id());
        dto.setVersion(dto.getVersion());
        return dto;
    }

    public static EmployeeCreatedPayload mapEntityToPayload(Employee entity) {
        return new EmployeeCreatedPayload(
                entity.getId(),
                entity.getUser_id(),
                entity.getEmployee_number(),
                entity.getFirst_name(),
                entity.getLast_name(),
                entity.getEmail(),
                entity.getDepartment_id(),
                entity.getManager_employee_id(),
                entity.getHire_date(),
                entity.getCreated_at(),
                entity.getUpdated_at(),
                entity.getVersion(),
                entity.getStatus()
        );
    }

}
