package com.example.erp.employeeservice.service.services;

import com.example.erp.employeeservice.domain.Employee;
import com.example.erp.employeeservice.dto.CreateEmployeeRequestDto;
import com.example.erp.employeeservice.dto.CreateEmployeeResponseDto;
import com.example.erp.employeeservice.events.DomainEvent;
import com.example.erp.employeeservice.events.EmployeeEventTypes;
import com.example.erp.employeeservice.events.IEmployeeEventPublisher;
import com.example.erp.employeeservice.exceptions.departmentNotFoundCustomException;
import com.example.erp.employeeservice.exceptions.managerUserNotFoundCustomException;
import com.example.erp.employeeservice.exceptions.userNotFoundCustomException;
import com.example.erp.employeeservice.mapper.CreateEmployeeMapper;
import com.example.erp.employeeservice.repository.IEmployeeRepository;
import com.example.erp.employeeservice.service.contracts.IDepartmentClientService;
import com.example.erp.employeeservice.service.contracts.IEmployeeService;
import com.example.erp.employeeservice.service.contracts.IUserClientService;
import org.springframework.stereotype.Service;

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
            throw new userNotFoundCustomException(dto.getUser_id());

        if (!userClientService.isUserExists(dto.getManager_employee_id()))
            throw new managerUserNotFoundCustomException(dto.getManager_employee_id());

        if (!departmentClientService.isDepartmentExists(dto.getDepartment_id()))
            throw new departmentNotFoundCustomException(dto.getDepartment_id());



        var entity = CreateEmployeeMapper.mapCreateToEntity(dto);
        var result  = employeeRepository.save(entity);
        publishCreateEvent(result);

        return CreateEmployeeMapper.mapEntityToCreate(result);

    }

    private void publishCreateEvent(Employee entity)
    {
        employeeEventPublisher.publish(
                DomainEvent.EmployeeEvent(EmployeeEventTypes.Employee_CREATED,
                        CreateEmployeeMapper.mapEntityToPayload(entity)));
    }
}
