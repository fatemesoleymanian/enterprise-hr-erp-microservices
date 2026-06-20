package com.example.erp.employeeservice.controllers;

import com.example.erp.employeeservice.controller.EmployeeController;
import com.example.erp.employeeservice.domain.Status;
import com.example.erp.employeeservice.dto.CreateEmployeeRequestDto;
import com.example.erp.employeeservice.dto.CreateEmployeeResponseDto;
import com.example.erp.employeeservice.dto.FindEmployeeResponseDto;
import com.example.erp.employeeservice.service.services.EmployeeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.UUID;


import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmployeeController.class)
public class EmployeeControllerTest
{
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmployeeService employeeService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void shouldCreateEmployeeSuccessfully() throws Exception {

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

        CreateEmployeeResponseDto response =
                new CreateEmployeeResponseDto(
                        UUID.randomUUID(),UUID.randomUUID(),
                        "EMP-001","Ali",
                        "Ahmadi","ali@gmail.com",
                        UUID.randomUUID(),UUID.randomUUID(),
                        OffsetDateTime.now(),OffsetDateTime.now(),
                        OffsetDateTime.now(),1,Status.ACTIVE);

        when(employeeService.create(any(CreateEmployeeRequestDto.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/employee/create")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void findById_whenEmployeeExists_shouldReturnEmployeeDto() throws Exception
    {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID departmentId = UUID.randomUUID();
        UUID managerEmployeeId = UUID.randomUUID();

        OffsetDateTime hireDate = OffsetDateTime.parse("2024-01-10T10:15:30+03:30");
        OffsetDateTime createdAt = OffsetDateTime.parse("2024-01-11T10:15:30+03:30");
        OffsetDateTime updatedAt = OffsetDateTime.parse("2024-01-12T10:15:30+03:30");

        FindEmployeeResponseDto responseDto = new FindEmployeeResponseDto();
        responseDto.setId(id);
        responseDto.setUser_id(userId);
        responseDto.setEmployee_number("EMP-1001");
        responseDto.setFirst_name("Ali");
        responseDto.setLast_name("Ahmadi");
        responseDto.setEmail("ali@test.com");
        responseDto.setDepartment_id(departmentId);
        responseDto.setManager_employee_id(managerEmployeeId);
        responseDto.setHire_date(hireDate);
        responseDto.setCreated_at(createdAt);
        responseDto.setUpdated_at(updatedAt);
        responseDto.setVersion(1);
        responseDto.setStatus(Status.ACTIVE);

        when(employeeService.findById(id)).thenReturn(responseDto);

        mockMvc.perform(get("/api/employee/findById")
                        .param("id", id.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Success")))
                .andExpect(jsonPath("$.data.id", is(id.toString())))
                .andExpect(jsonPath("$.data.user_id", is(userId.toString())))
                .andExpect(jsonPath("$.data.employee_number", is("EMP-1001")))
                .andExpect(jsonPath("$.data.first_name", is("Ali")))
                .andExpect(jsonPath("$.data.last_name", is("Ahmadi")))
                .andExpect(jsonPath("$.data.email", is("ali@test.com")))
                .andExpect(jsonPath("$.data.department_id", is(departmentId.toString())))
                .andExpect(jsonPath("$.data.manager_employee_id", is(managerEmployeeId.toString())))
                .andExpect(jsonPath("$.data.hire_date", is("2024-01-10T10:15:30+03:30")))
                .andExpect(jsonPath("$.data.created_at", is("2024-01-11T10:15:30+03:30")))
                .andExpect(jsonPath("$.data.updated_at", is("2024-01-12T10:15:30+03:30")))
                .andExpect(jsonPath("$.data.version", is(1)))
                .andExpect(jsonPath("$.data.status", is("ACTIVE")));

    }

    @Test
    @WithMockUser
    void findAll_shouldReturnListOfEmployees() throws Exception {

        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        FindEmployeeResponseDto dto = new FindEmployeeResponseDto();
        dto.setId(id);
        dto.setUser_id(userId);
        dto.setEmployee_number("EMP-1001");
        dto.setFirst_name("Ali");
        dto.setLast_name("Ahmadi");
        dto.setEmail("ali@test.com");
        dto.setVersion(1);
        dto.setStatus(Status.ACTIVE);

        ArrayList<FindEmployeeResponseDto> list = new ArrayList<>();
        list.add(dto);

        when(employeeService.findAll()).thenReturn(list);

        mockMvc.perform(get("/api/employee/findAll")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data[0].id").value(id.toString()))
                .andExpect(jsonPath("$.data[0].user_id").value(userId.toString()))
                .andExpect(jsonPath("$.data[0].employee_number").value("EMP-1001"))
                .andExpect(jsonPath("$.data[0].first_name").value("Ali"))
                .andExpect(jsonPath("$.data[0].last_name").value("Ahmadi"))
                .andExpect(jsonPath("$.data[0].email").value("ali@test.com"))
                .andExpect(jsonPath("$.data[0].version").value(1))
                .andExpect(jsonPath("$.data[0].status").value("ACTIVE"));

        verify(employeeService, times(1)).findAll();
    }
}
