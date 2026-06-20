package com.example.erp.employeeservice.mapper;

import com.example.erp.employeeservice.domain.Employee;
import com.example.erp.employeeservice.domain.Status;
import com.example.erp.employeeservice.dto.*;
import com.example.erp.employeeservice.events.EmployeeCreatedPayload;
import com.example.erp.employeeservice.events.EmployeeDepartmentUpdatedPayload;
import com.example.erp.employeeservice.events.EmployeeStatusUpdatedPayload;
import com.example.erp.employeeservice.events.EmployeeUpdatedPayload;

import java.time.OffsetDateTime;
import java.util.UUID;


public class UpdateEmployeeMapper
{
    public static Employee mapUpdateToEntity(UpdateEmployeeRequestDto updateEmployeeRequestDto)
    {
        var entity = new Employee();
        entity.setManager_employee_id(updateEmployeeRequestDto.getManager_employee_id());
        entity.setDepartment_id(updateEmployeeRequestDto.getDepartment_id());
        entity.setFirst_name(updateEmployeeRequestDto.getFirst_name());
        entity.setLast_name(updateEmployeeRequestDto.getLast_name());
        entity.setEmail(updateEmployeeRequestDto.getEmail());
        entity.setEmployee_number(updateEmployeeRequestDto.getEmployee_number());
        entity.setUpdated_at(OffsetDateTime.now());
        entity.setCreated_at(updateEmployeeRequestDto.getCreated_at());
        entity.setStatus(updateEmployeeRequestDto.getStatus());
        entity.setEmail(updateEmployeeRequestDto.getEmail());

        return entity;
    }

    public static UpdateEmployeeResponseDto mapEntityToUpdate(Employee entity)
    {
        var dto = new UpdateEmployeeResponseDto();
        dto.setUpdated_at(entity.getUpdated_at());
        dto.setId(entity.getId());
        dto.setEmail(entity.getEmail());
        dto.setStatus(entity.getStatus());
        dto.setFirst_name(entity.getFirst_name());
        dto.setEmployee_number(entity.getEmployee_number());
        dto.setDepartment_id(entity.getDepartment_id());
        dto.setHire_date(entity.getHire_date());
        dto.setUser_id(entity.getUser_id());
        dto.setManager_employee_id(entity.getManager_employee_id());
        dto.setVersion(entity.getVersion());
        dto.setCreated_at(entity.getCreated_at());
        dto.setEmployee_number(entity.getEmployee_number());
        return dto;
    }

    public static EmployeeUpdatedPayload mapEntityToPayload(Employee entity) {
        return new EmployeeUpdatedPayload(
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

    public static UpdateStatusEmployeeResponseDto mapEntityToUpdateStatus(Employee entity)
    {
        var dto = new UpdateStatusEmployeeResponseDto();

        dto.setUpdatedAt(entity.getUpdated_at());
        dto.setId(entity.getId());
        dto.setStatus(String.valueOf(entity.getStatus()));

        return dto;

    }

    public static EmployeeStatusUpdatedPayload mapEntityToStatusPayload(Employee entity)
    {
        return new EmployeeStatusUpdatedPayload
                (entity.getId(),entity.getStatus(),entity.getUpdated_at());
    }

    public static UpdateDepartmentEmployeeResponseDto mapEntityToUpdateDepartment(Employee entity)
    {
        var dto = new UpdateDepartmentEmployeeResponseDto();
        dto.setId(entity.getId());
        dto.setDepartmentId(entity.getDepartment_id());
        dto.setUpdatedAt(entity.getUpdated_at());

        return dto;
    }

    public static EmployeeDepartmentUpdatedPayload mapEntityToDepartmentUpdatedPayload(Employee entity)
    {
        return new EmployeeDepartmentUpdatedPayload(entity.getId(),entity.getDepartment_id(),entity.getUpdated_at());
    }


}
