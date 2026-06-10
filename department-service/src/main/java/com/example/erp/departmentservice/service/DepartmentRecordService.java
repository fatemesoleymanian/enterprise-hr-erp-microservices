package com.example.erp.departmentservice.service;

import com.example.erp.departmentservice.dto.CreateRequestRecordDto;
import com.example.erp.departmentservice.dto.CreateResponseRecordDto;
import com.example.erp.departmentservice.exceptions.DepartmentDuplicateNameCustomException;
import com.example.erp.departmentservice.mapper.DepartmentRecordMapper;
import com.example.erp.departmentservice.repository.DepartmentRecordRepository;
import org.springframework.stereotype.Service;

@Service
public class DepartmentRecordService
{
    private final DepartmentRecordRepository departmentRecordRepository;

    public DepartmentRecordService(
            DepartmentRecordRepository departmentRecordRepository)
    {
        this.departmentRecordRepository = departmentRecordRepository;
    }

    public CreateResponseRecordDto create(CreateRequestRecordDto dto)
    {
        if (existByName(dto.getName()))
            throw new DepartmentDuplicateNameCustomException(dto.getName());

        var entity = DepartmentRecordMapper.mapCreateToEntity(dto);
        var savedEntity = departmentRecordRepository.save(entity);

        return DepartmentRecordMapper.mapEntityToCreate(savedEntity);
    }

    public boolean existByName(String name) {
        return departmentRecordRepository.existsByName(name);
    }
}
