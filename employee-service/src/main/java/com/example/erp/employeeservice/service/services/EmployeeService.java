package com.example.erp.employeeservice.service.services;

import com.example.erp.employeeservice.domain.Employee;
import com.example.erp.employeeservice.dto.CreateEmployeeRequestDto;
import com.example.erp.employeeservice.dto.CreateEmployeeResponseDto;
import com.example.erp.employeeservice.dto.FindEmployeeResponseDto;
import com.example.erp.employeeservice.events.DomainEvent;
import com.example.erp.employeeservice.events.EmployeeEventTypes;
import com.example.erp.employeeservice.events.IEmployeeEventPublisher;
import com.example.erp.employeeservice.exceptions.DepartmentNotFoundCustomException;
import com.example.erp.employeeservice.exceptions.EmployeeNotFoundCustomException;
import com.example.erp.employeeservice.exceptions.ManagerUserNotFoundCustomException;
import com.example.erp.employeeservice.exceptions.UserNotFoundCustomException;
import com.example.erp.employeeservice.mapper.CreateEmployeeMapper;
import com.example.erp.employeeservice.mapper.FindEmployeeMapper;
import com.example.erp.employeeservice.repository.IEmployeeRepository;
import com.example.erp.employeeservice.service.contracts.IDepartmentClientService;
import com.example.erp.employeeservice.service.contracts.IEmployeeService;
import com.example.erp.employeeservice.service.contracts.IUserClientService;
import org.springframework.stereotype.Service;

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




    private void publishCreateEvent(Employee entity)
    {
        employeeEventPublisher.publish(
                DomainEvent.EmployeeEvent(EmployeeEventTypes.Employee_CREATED,
                        CreateEmployeeMapper.mapEntityToPayload(entity)));
    }
}
