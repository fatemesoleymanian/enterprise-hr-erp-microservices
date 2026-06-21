package com.example.erp.employeeservice.service.services;

import com.example.erp.employeeservice.domain.Employee;
import com.example.erp.employeeservice.domain.Status;
import com.example.erp.employeeservice.dto.*;
import com.example.erp.employeeservice.events.DomainEvent;
import com.example.erp.employeeservice.events.EmployeeEventTypes;
import com.example.erp.employeeservice.events.IEmployeeEventPublisher;
import com.example.erp.employeeservice.exceptions.DepartmentNotFoundCustomException;
import com.example.erp.employeeservice.exceptions.EmployeeNotFoundCustomException;
import com.example.erp.employeeservice.exceptions.ManagerUserNotFoundCustomException;
import com.example.erp.employeeservice.exceptions.UserNotFoundCustomException;
import com.example.erp.employeeservice.mapper.CreateEmployeeMapper;
import com.example.erp.employeeservice.mapper.FindEmployeeMapper;
import com.example.erp.employeeservice.mapper.UpdateEmployeeMapper;
import com.example.erp.employeeservice.repository.IEmployeeRepository;
import com.example.erp.employeeservice.service.contracts.IDepartmentClientService;
import com.example.erp.employeeservice.service.contracts.IEmployeeService;
import com.example.erp.employeeservice.service.contracts.IUserClientService;
import org.hibernate.sql.Update;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.UUID;


@Service
public class EmployeeService implements IEmployeeService
{
    private final IEmployeeRepository employeeRepository;
    private final IEmployeeEventPublisher employeeEventPublisher;
    private final IUserClientService  userClientService;
    private final IDepartmentClientService departmentClientService;

    public EmployeeService(IEmployeeRepository employeeRepository, IEmployeeEventPublisher employeeEventPublisher, IUserClientService userClientService, IDepartmentClientService departmentClientService) {
        this.employeeRepository = employeeRepository;
        this.employeeEventPublisher = employeeEventPublisher;
        this.userClientService = userClientService;
        this.departmentClientService = departmentClientService;
    }

    @Override
    public CreateEmployeeResponseDto create(CreateEmployeeRequestDto dto)
    {

        if (!userClientService.isUserExists(dto.getUser_id()))
            throw new UserNotFoundCustomException(dto.getUser_id());

        if (!userClientService.isUserExists(dto.getManager_employee_id()))
            throw new ManagerUserNotFoundCustomException(dto.getManager_employee_id());

        if (!departmentClientService.isDepartmentExists(dto.getDepartment_id()))
            throw new DepartmentNotFoundCustomException(dto.getDepartment_id());



        var entity = CreateEmployeeMapper.mapCreateToEntity(dto);
        var result  = employeeRepository.save(entity);
        publishCreateEvent(result);

        return CreateEmployeeMapper.mapEntityToCreate(result);

    }

    @Override
    public FindEmployeeResponseDto findById(UUID id) {

        Employee entity = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundCustomException(id));

        return FindEmployeeMapper.mapEntityToFind(entity);
    }

    @Override
    public ArrayList<FindEmployeeResponseDto> findAll() {
        return new ArrayList<>(
                employeeRepository.findAll()
                        .stream()
                        .map(FindEmployeeMapper::mapEntityToFind)
                        .toList()
        );
    }

    @Override
    public UpdateEmployeeResponseDto update(UUID id, UpdateEmployeeRequestDto updateEmployeeRequestDto) {
        Employee entity = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundCustomException(id));

        var updatedEmployee = UpdateEmployeeMapper.mapUpdateToEntity(updateEmployeeRequestDto);
        var result = employeeRepository.save(updatedEmployee);

        publishUpdateEvent(result);

        return UpdateEmployeeMapper.mapEntityToUpdate(result);


    }

    @Override
    public UpdateStatusEmployeeResponseDto updateStatus(UUID id, Status status)
    {
        Employee entity = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundCustomException(id));

        entity.setStatus(status);
        entity.setUpdated_at(OffsetDateTime.now());
        var result = employeeRepository.save(entity);
        publishUpdateStatusEvent(result);

        return UpdateEmployeeMapper.mapEntityToUpdateStatus(entity);

    }

    @Override
    public UpdateDepartmentEmployeeResponseDto updateDepartment(UUID id, UUID department_id)
    {
        Employee entity = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundCustomException(id));

        entity.setDepartment_id(department_id);
        entity.setUpdated_at(OffsetDateTime.now());
        var result = employeeRepository.save(entity);
        publishUpdateStatusEvent(result);

        return UpdateEmployeeMapper.mapEntityToUpdateDepartment(entity);

    }


    private void publishCreateEvent(Employee entity)
    {
        employeeEventPublisher.publish(
                DomainEvent.EmployeeEvent(EmployeeEventTypes.Employee_CREATED,
                        CreateEmployeeMapper.mapEntityToPayload(entity)));
    }

    private void publishUpdateEvent(Employee entity)
    {
        employeeEventPublisher.publish(
                DomainEvent.EmployeeEvent(EmployeeEventTypes.Employee_UPDATED,
                        UpdateEmployeeMapper.mapEntityToPayload(entity)));
    }

    private void publishUpdateStatusEvent(Employee entity)
    {
        employeeEventPublisher.publish(
                DomainEvent.EmployeeEvent(EmployeeEventTypes.Employee_Status_UPDATED,
                        UpdateEmployeeMapper.mapEntityToStatusPayload(entity))
        );
    }

    private void publishUpdateDepartmentEvent(Employee entity)
    {
        employeeEventPublisher.publish(
                DomainEvent.EmployeeEvent(EmployeeEventTypes.Employee_Department_UPDATED,
                        UpdateEmployeeMapper.mapEntityToDepartmentUpdatedPayload(entity))
        );
    }

}
