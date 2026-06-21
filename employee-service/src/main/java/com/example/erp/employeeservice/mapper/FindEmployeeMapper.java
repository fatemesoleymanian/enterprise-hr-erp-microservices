package com.example.erp.employeeservice.mapper;

import com.example.erp.employeeservice.domain.Employee;
import com.example.erp.employeeservice.dto.FindEmployeeResponseDto;


public class FindEmployeeMapper
{
    public static FindEmployeeResponseDto mapEntityToFind( Employee entity)
    {
        var dto = new FindEmployeeResponseDto();
        dto.setCreated_at(entity.getCreated_at());
        dto.setUpdated_at(entity.getUpdated_at());
        dto.setId(entity.getId());
        dto.setEmail(entity.getEmail());
        dto.setStatus(entity.getStatus());
        dto.setFirst_name(entity.getFirst_name());
        dto.setEmployee_number(entity.getEmployee_number());
        dto.setLast_name(entity.getLast_name());
        dto.setDepartment_id(entity.getDepartment_id());
        dto.setHire_date(entity.getHire_date());
        dto.setUser_id(entity.getUser_id());
        dto.setManager_employee_id(entity.getManager_employee_id());
        dto.setVersion(entity.getVersion());
        return dto;
    }
}
