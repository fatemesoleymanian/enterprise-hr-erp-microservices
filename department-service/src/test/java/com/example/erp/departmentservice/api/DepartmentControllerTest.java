package com.example.erp.departmentservice.api;

import com.example.erp.departmentservice.controller.DepartmentRecordController;
import com.example.erp.departmentservice.dto.CreateRequestRecordDto;
import com.example.erp.departmentservice.dto.CreateResponseRecordDto;
import com.example.erp.departmentservice.service.DepartmentRecordService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean; // جدید برای اسپرینگ 3.4
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath; // اصلاح شد
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DepartmentRecordController.class)
class DepartmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DepartmentRecordService departmentRecordService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateDepartment() throws Exception {

        CreateRequestRecordDto request =
                new CreateRequestRecordDto("IT", "Information Technology");

        CreateResponseRecordDto response =
                new CreateResponseRecordDto(
                        UUID.randomUUID(),
                        "IT",
                        "Information Technology",
                        OffsetDateTime.now(),
                        OffsetDateTime.now()
                );

        when(departmentRecordService.create(any(CreateRequestRecordDto.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/department/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.name").value("IT"))
                .andExpect(jsonPath("$.data.description").value("Information Technology"));

        verify(departmentRecordService).create(any(CreateRequestRecordDto.class));
    }
}
