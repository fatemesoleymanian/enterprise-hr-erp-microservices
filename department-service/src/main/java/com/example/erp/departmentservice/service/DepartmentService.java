package com.example.erp.departmentservice.service;

import com.example.erp.departmentservice.dto.*;
import com.example.erp.departmentservice.exceptions.DepartmentDuplicateNameCustomException;
import com.example.erp.departmentservice.exceptions.DepartmentFindByIdNullCustomException;
import com.example.erp.departmentservice.mapper.CreateDepartmentMapper;
import com.example.erp.departmentservice.mapper.FindDepartmentMapper;
import com.example.erp.departmentservice.mapper.UpdateDepartmentMapper;
import com.example.erp.departmentservice.repository.DepartmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class DepartmentService
{
    private final DepartmentRepository departmentRecordRepository;

    public DepartmentService(
            DepartmentRepository departmentRecordRepository)
    {
        this.departmentRecordRepository = departmentRecordRepository;
    }

    public CreateDepartmentResponseDto create(CreateDepartmentRequestDto dto)
    {
        if (existByName(dto.getName()))
            throw new DepartmentDuplicateNameCustomException(dto.getName());

        var entity = CreateDepartmentMapper.mapCreateToEntity(dto);
        var savedEntity = departmentRecordRepository.save(entity);

        return CreateDepartmentMapper.mapEntityToCreate(savedEntity);
    }

    public UpdateDepartmentResponseDto update(UpdateDepartmentRequestDto dto) {

        var entity = departmentRecordRepository.findById(dto.getId())
                .orElseThrow(() -> new DepartmentFindByIdNullCustomException(dto.getId()));

        if (existByName(dto.getName()) && !Objects.equals(entity.getName(), dto.getName())) {
            throw new DepartmentDuplicateNameCustomException(dto.getName());
        }


        var newEntity = UpdateDepartmentMapper.mapUpdateToEntity(dto);

        var savedEntity = departmentRecordRepository.save(newEntity);

        return UpdateDepartmentMapper.mapEntityToUpdate(savedEntity);
    }






    public boolean existByName(String name) {
        return departmentRecordRepository.existsByName(name);
    }

    public FindDepartmentResponseDto findById(UUID id) {
        return departmentRecordRepository.findById(id)
                .map(FindDepartmentMapper::mapEntityToFind)
                .orElseThrow(() -> new DepartmentFindByIdNullCustomException(id));
    }


    public List<FindDepartmentResponseDto> findAll() {
        return departmentRecordRepository.findAll()
                .stream()
                .map(FindDepartmentMapper::mapEntityToFind)
                .toList();
    }



}
