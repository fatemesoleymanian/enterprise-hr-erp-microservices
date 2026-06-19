package com.example.erp.employeeservice.controllers;

import com.example.erp.employeeservice.controller.EmployeeController;
import com.example.erp.employeeservice.domain.Status;
import com.example.erp.employeeservice.dto.CreateEmployeeRequestDto;
import com.example.erp.employeeservice.dto.CreateEmployeeResponseDto;
import com.example.erp.employeeservice.service.services.EmployeeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
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
}
