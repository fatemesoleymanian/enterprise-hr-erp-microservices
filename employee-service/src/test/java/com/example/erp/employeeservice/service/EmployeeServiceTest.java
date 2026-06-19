package com.example.erp.employeeservice.service;

import com.example.erp.employeeservice.domain.Employee;
import com.example.erp.employeeservice.domain.Status;
import com.example.erp.employeeservice.dto.CreateEmployeeRequestDto;
import com.example.erp.employeeservice.exceptions.departmentNotFoundCustomException;
import com.example.erp.employeeservice.exceptions.userNotFoundCustomException;
import com.example.erp.employeeservice.service.services.EmployeeService;
import com.example.erp.employeeservice.repository.IEmployeeRepository;
import com.example.erp.employeeservice.events.IEmployeeEventPublisher;
import com.example.erp.employeeservice.service.contracts.IUserClientService;
import com.example.erp.employeeservice.service.contracts.IDepartmentClientService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class EmployeeServiceTest {

    @Mock
    private IEmployeeRepository employeeRepository;

    @Mock
    private IEmployeeEventPublisher employeeEventPublisher;

    @Mock
    private IUserClientService userClientService;

    @Mock
    private IDepartmentClientService departmentClientService;

    private EmployeeService employeeService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        employeeService = new EmployeeService(
                employeeRepository,
                employeeEventPublisher,
                userClientService,
                departmentClientService
        );
    }

    @Test
    void shouldCreateEmployeeSuccessfully() {

        UUID userId = UUID.randomUUID();
        UUID managerId = UUID.randomUUID();
        UUID departmentId = UUID.randomUUID();

        CreateEmployeeRequestDto request = new CreateEmployeeRequestDto(
                userId,
                "EMP-001",
                "Ali",
                "Ahmadi",
                "ali@test.com",
                departmentId,
                managerId,
                OffsetDateTime.now(),
                Status.ACTIVE,
                1
        );

        Employee savedEmployee = new Employee();
        savedEmployee.setId(UUID.randomUUID());
        savedEmployee.setUser_id(userId);

        when(userClientService.isUserExists(userId)).thenReturn(true);
        when(userClientService.isUserExists(managerId)).thenReturn(true);
        when(departmentClientService.isDepartmentExists(departmentId)).thenReturn(true);
        when(employeeRepository.save(any(Employee.class))).thenReturn(savedEmployee);

        var response = employeeService.create(request);

        assertNotNull(response);

        verify(employeeRepository).save(any(Employee.class));
        verify(employeeEventPublisher).publish(any());
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {

        UUID userId = UUID.randomUUID();

        CreateEmployeeRequestDto request = mock(CreateEmployeeRequestDto.class);

        when(request.getUser_id()).thenReturn(userId);
        when(userClientService.isUserExists(userId)).thenReturn(false);

        assertThrows(userNotFoundCustomException.class,
                () -> employeeService.create(request));

        verify(employeeRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenDepartmentNotFound() {

        UUID userId = UUID.randomUUID();
        UUID managerId = UUID.randomUUID();
        UUID departmentId = UUID.randomUUID();

        CreateEmployeeRequestDto request = mock(CreateEmployeeRequestDto.class);

        when(request.getUser_id()).thenReturn(userId);
        when(request.getManager_employee_id()).thenReturn(managerId);
        when(request.getDepartment_id()).thenReturn(departmentId);

        when(userClientService.isUserExists(userId)).thenReturn(true);
        when(userClientService.isUserExists(managerId)).thenReturn(true);
        when(departmentClientService.isDepartmentExists(departmentId)).thenReturn(false);

        assertThrows(departmentNotFoundCustomException.class,
                () -> employeeService.create(request));

        verify(employeeRepository, never()).save(any());
    }
}

