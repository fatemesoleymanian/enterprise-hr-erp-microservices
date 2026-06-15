package com.example.erp.departmentservice.service.Services;

import com.example.erp.departmentservice.domain.Department;
import com.example.erp.departmentservice.dto.*;
import com.example.erp.departmentservice.events.DepartmentEventTypes;
import com.example.erp.departmentservice.events.DomainEvent;
import com.example.erp.departmentservice.events.IDepartmentEventPublisher;
import com.example.erp.departmentservice.exceptions.DepartmentDuplicateNameCustomException;
import com.example.erp.departmentservice.exceptions.DepartmentFindByIdNullCustomException;
import com.example.erp.departmentservice.exceptions.UserNotFoundCustomException;
import com.example.erp.departmentservice.mapper.AssignDepartmentUpdateMapper;
import com.example.erp.departmentservice.mapper.CreateDepartmentMapper;
import com.example.erp.departmentservice.mapper.FindDepartmentMapper;
import com.example.erp.departmentservice.mapper.UpdateDepartmentMapper;
import com.example.erp.departmentservice.repository.IDepartmentRepository;
import com.example.erp.departmentservice.service.Contracts.IDepartmentService;
import com.example.erp.departmentservice.service.Contracts.IUserClientService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class DepartmentService implements IDepartmentService {
    private final IDepartmentRepository departmentRecordRepository;
    private final IDepartmentEventPublisher departmentEventPublisher;
    private final IUserClientService userClientService;

    public DepartmentService(
            IDepartmentRepository departmentRecordRepository, IDepartmentEventPublisher departmentEventPublisher, IUserClientService userClientService)
    {
        this.departmentRecordRepository = departmentRecordRepository;
        this.departmentEventPublisher = departmentEventPublisher;
        this.userClientService = userClientService;
    }

@Transactional
    public CreateDepartmentResponseDto create(CreateDepartmentRequestDto dto)
    {

        if (existByName(dto.getName()))
            throw new DepartmentDuplicateNameCustomException(dto.getName());


        var entity = CreateDepartmentMapper.mapCreateToEntity(dto);
        var savedEntity = departmentRecordRepository.save(entity);

        publishCreateEvent(savedEntity);


        return CreateDepartmentMapper.mapEntityToCreate(savedEntity);
    }

@Transactional
    public UpdateDepartmentResponseDto update(UpdateDepartmentRequestDto dto) {

        var entity = departmentRecordRepository.findById(dto.getId())
                .orElseThrow(() -> new DepartmentFindByIdNullCustomException(dto.getId()));

        if (existByName(dto.getName()) && !Objects.equals(entity.getName(), dto.getName())) {
            throw new DepartmentDuplicateNameCustomException(dto.getName());
        }


        var newEntity = UpdateDepartmentMapper.mapUpdateToEntity(dto);

        var savedEntity = departmentRecordRepository.save(newEntity);

        publishUpdateEvent(newEntity);

        return UpdateDepartmentMapper.mapEntityToUpdate(savedEntity);
    }
@Transactional
    public AssignDepartmentUpdateResponseDto assignDepartmentToManager(UUID id, UUID managerId) {

        var entity = departmentRecordRepository.findById(id)
                .orElseThrow(() -> new DepartmentFindByIdNullCustomException(id));

        if (!userClientService.isUserExists(managerId))
            throw new UserNotFoundCustomException(managerId);

        entity.setManagerUserId(managerId);
        entity.setUpdatedAt(OffsetDateTime.now());

        var result = departmentRecordRepository.save(entity);

        publishAssignEvent(result);

        return AssignDepartmentUpdateMapper.mapEntityToUpdate(result);
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


    private void publishCreateEvent(Department entity)
    {
        departmentEventPublisher.publish
                (DomainEvent.departmentEvent(
                        DepartmentEventTypes.DEPARTMENT_CREATED,
                        CreateDepartmentMapper.mapEntityToEvent(entity)));
    }

    private void publishAssignEvent(Department entity)
    {
        departmentEventPublisher.publish
                (DomainEvent.departmentEvent(
                        DepartmentEventTypes.DEPARTMENT_ASSIGNED,
                        AssignDepartmentUpdateMapper.mapEntityToEvent(entity)));
    }

    private void publishUpdateEvent(Department entity)
    {
        departmentEventPublisher.publish
                (DomainEvent.departmentEvent(
                        DepartmentEventTypes.DEPARTMENT_UPDATED,
                        UpdateDepartmentMapper.mapEntityToEvent(entity)));
    }






}
