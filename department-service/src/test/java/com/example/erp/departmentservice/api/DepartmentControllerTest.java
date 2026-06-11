package com.example.erp.departmentservice.api;

import com.example.erp.departmentservice.controller.DepartmentController;
import com.example.erp.departmentservice.dto.*;
import com.example.erp.departmentservice.exceptions.DepartmentFindByIdNullCustomException;
import com.example.erp.departmentservice.service.DepartmentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DepartmentController.class)
class DepartmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DepartmentService departmentRecordService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateDepartment() throws Exception {

        CreateDepartmentRequestDto request =
                new CreateDepartmentRequestDto("IT", "Information Technology");

        CreateDepartmentResponseDto response =
                new CreateDepartmentResponseDto(
                        UUID.randomUUID(),
                        "IT",
                        "Information Technology",
                        OffsetDateTime.now(),
                        OffsetDateTime.now()
                );

        when(departmentRecordService.create(any(CreateDepartmentRequestDto.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/department/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.name").value("IT"))
                .andExpect(jsonPath("$.data.description").value("Information Technology"));

        verify(departmentRecordService).create(any(CreateDepartmentRequestDto.class));
    }

    @Test
    void shouldUpdateDepartment() throws Exception {
        UUID id = UUID.randomUUID();
        OffsetDateTime createdAt = OffsetDateTime.now().minusDays(1);
        OffsetDateTime updatedAt = OffsetDateTime.now();

        UpdateDepartmentRequestDto request =
                new UpdateDepartmentRequestDto(id, "IT", "Information Technology");

        UpdateDepartmentResponseDto response =
                new UpdateDepartmentResponseDto(
                        id,
                        "IT",
                        "Information Technology",
                        createdAt,
                        updatedAt
                );

        when(departmentRecordService.update(any(UpdateDepartmentRequestDto.class)))
                .thenReturn(response);

        mockMvc.perform(put("/api/department/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(id.toString()))
                .andExpect(jsonPath("$.data.name").value("IT"))
                .andExpect(jsonPath("$.data.description").value("Information Technology"));

        verify(departmentRecordService).update(any(UpdateDepartmentRequestDto.class));
    }

    @Test
    void shouldReturnNotFoundWhenDepartmentDoesNotExist() throws Exception {
        UUID id = UUID.randomUUID();

        when(departmentRecordService.findById(id))
                .thenThrow(new DepartmentFindByIdNullCustomException(id));

        mockMvc.perform(get("/api/department/find/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(departmentRecordService).findById(id);
    }

    @Test
    void shouldFindAllDepartments() throws Exception {
        FindDepartmentResponseDto dept1 = new FindDepartmentResponseDto(
                UUID.randomUUID(), "IT", "Information Technology");
        FindDepartmentResponseDto dept2 = new FindDepartmentResponseDto(
                UUID.randomUUID(), "HR", "Human Resources");

        List<FindDepartmentResponseDto> allDepartments = List.of(dept1, dept2);

        when(departmentRecordService.findAll()).thenReturn(allDepartments);

        mockMvc.perform(get("/api/department/findall")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].name").value("IT"))
                .andExpect(jsonPath("$.data[0].description").value("Information Technology"))
                .andExpect(jsonPath("$.data[1].name").value("HR"))
                .andExpect(jsonPath("$.data[1].description").value("Human Resources"));

        verify(departmentRecordService).findAll();
    }




}
