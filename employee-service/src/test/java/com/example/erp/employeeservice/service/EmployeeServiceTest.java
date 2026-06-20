package com.example.erp.employeeservice.service;

import com.example.erp.employeeservice.domain.Employee;
import com.example.erp.employeeservice.domain.Status;
import com.example.erp.employeeservice.dto.CreateEmployeeRequestDto;
import com.example.erp.employeeservice.dto.FindEmployeeResponseDto;
import com.example.erp.employeeservice.exceptions.DepartmentNotFoundCustomException;
import com.example.erp.employeeservice.exceptions.EmployeeNotFoundCustomException;
import com.example.erp.employeeservice.exceptions.UserNotFoundCustomException;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

        assertThrows(UserNotFoundCustomException.class,
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

        assertThrows(DepartmentNotFoundCustomException.class,
                () -> employeeService.create(request));

        verify(employeeRepository, never()).save(any());
    }

    @Test
    void findById_whenEmployeeExists_shouldReturnEmployeeDto() {
        UUID employeeId = UUID.randomUUID();

        Employee employee = new Employee();
        employee.setId(employeeId);
        employee.setFirst_name("Ali");
        employee.setLast_name("Ahmadi");
        employee.setEmail("ali.ahmadi@example.com");

        when(employeeRepository.findById(employeeId))
                .thenReturn(Optional.of(employee));

        FindEmployeeResponseDto result = employeeService.findById(employeeId);

        assertNotNull(result);

        assertEquals(employeeId, result.getId());
        assertEquals("Ali", result.getFirst_name());
        assertEquals("Ahmadi", result.getLast_name());
        assertEquals("ali.ahmadi@example.com", result.getEmail());

        verify(employeeRepository, times(1)).findById(employeeId);
        verifyNoMoreInteractions(employeeRepository);
    }

    @Test
    void findById_whenEmployeeDoesNotExist_shouldThrowEmployeeNotFoundCustomException() {
        // Arrange
        UUID employeeId = UUID.randomUUID();

        when(employeeRepository.findById(employeeId))
                .thenReturn(Optional.empty());

        EmployeeNotFoundCustomException exception = assertThrows(
                EmployeeNotFoundCustomException.class,
                () -> employeeService.findById(employeeId)
        );

        assertNotNull(exception);

        verify(employeeRepository, times(1)).findById(employeeId);
        verifyNoMoreInteractions(employeeRepository);
    }

    @Test
    void findAll_shouldReturnListOfFindEmployeeResponseDto() {
        UUID id1 = UUID.randomUUID();
        UUID userId1 = UUID.randomUUID();
        UUID departmentId1 = UUID.randomUUID();
        UUID managerEmployeeId1 = UUID.randomUUID();

        UUID id2 = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();
        UUID departmentId2 = UUID.randomUUID();
        UUID managerEmployeeId2 = UUID.randomUUID();

        Employee employee1 = new Employee();
        employee1.setId(id1);
        employee1.setUser_id(userId1);
        employee1.setEmployee_number("EMP-1001");
        employee1.setFirst_name("Ali");
        employee1.setLast_name("Ahmadi");
        employee1.setEmail("ali@test.com");
        employee1.setDepartment_id(departmentId1);
        employee1.setManager_employee_id(managerEmployeeId1);
        employee1.setStatus(Status.ACTIVE);
        employee1.setVersion(1);

        Employee employee2 = new Employee();
        employee2.setId(id2);
        employee2.setUser_id(userId2);
        employee2.setEmployee_number("EMP-1002");
        employee2.setFirst_name("Reza");
        employee2.setLast_name("Karimi");
        employee2.setEmail("reza@test.com");
        employee2.setDepartment_id(departmentId2);
        employee2.setManager_employee_id(managerEmployeeId2);
        employee2.setStatus(Status.ACTIVE);
        employee2.setVersion(1);

        when(employeeRepository.findAll()).thenReturn(List.of(employee1, employee2));

        ArrayList<FindEmployeeResponseDto> result = employeeService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertInstanceOf(ArrayList.class, result);

        FindEmployeeResponseDto first = result.get(0);

        assertEquals(id1, first.getId());
        assertEquals(userId1, first.getUser_id());
        assertEquals("EMP-1001", first.getEmployee_number());
        assertEquals("Ali", first.getFirst_name());
        assertEquals("Ahmadi", first.getLast_name());
        assertEquals("ali@test.com", first.getEmail());
        assertEquals(departmentId1, first.getDepartment_id());
        assertEquals(managerEmployeeId1, first.getManager_employee_id());
        assertEquals(Status.ACTIVE, first.getStatus());
        assertEquals(1, first.getVersion());

        FindEmployeeResponseDto second = result.get(1);

        assertEquals(id2, second.getId());
        assertEquals(userId2, second.getUser_id());
        assertEquals("EMP-1002", second.getEmployee_number());
        assertEquals("Reza", second.getFirst_name());
        assertEquals("Karimi", second.getLast_name());
        assertEquals("reza@test.com", second.getEmail());
        assertEquals(departmentId2, second.getDepartment_id());
        assertEquals(managerEmployeeId2, second.getManager_employee_id());
        assertEquals(Status.ACTIVE, second.getStatus());
        assertEquals(1, second.getVersion());

        verify(employeeRepository, times(1)).findAll();
        verifyNoMoreInteractions(employeeRepository);
    }
}

