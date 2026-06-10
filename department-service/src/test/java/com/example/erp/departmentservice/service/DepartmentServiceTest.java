package com.example.erp.departmentservice.service;

import com.example.erp.departmentservice.domain.DepartmentRecord;
import com.example.erp.departmentservice.dto.CreateRequestRecordDto;
import com.example.erp.departmentservice.dto.CreateResponseRecordDto;
import com.example.erp.departmentservice.exceptions.DepartmentDuplicateNameCustomException;
import com.example.erp.departmentservice.repository.DepartmentRecordRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DepartmentServiceTest {

    @Mock
    private DepartmentRecordRepository repository;

    @InjectMocks
    private DepartmentRecordService service;

    @Test
    void shouldCreateDepartment() {

        CreateRequestRecordDto request =
                new CreateRequestRecordDto(
                        "IT",
                        "Information Technology"
                );

        DepartmentRecord savedEntity = new DepartmentRecord();
        savedEntity.setId(UUID.randomUUID());
        savedEntity.setName("IT");
        savedEntity.setDescription("Information Technology");

        when(repository.save(any(DepartmentRecord.class)))
                .thenReturn(savedEntity);

        CreateResponseRecordDto result = service.create(request);

        assertNotNull(result);

        verify(repository).save(any(DepartmentRecord.class));
    }

    @Test
    void shouldCreateDepartmentWhenNameDoesNotExist() {

        CreateRequestRecordDto request =
                new CreateRequestRecordDto("IT", "Information Technology");

        DepartmentRecord saved = new DepartmentRecord();
        saved.setId(UUID.randomUUID());
        saved.setName("IT");

        when(repository.existsByName("IT")).thenReturn(false);
        when(repository.save(any(DepartmentRecord.class))).thenReturn(saved);

        CreateResponseRecordDto result = service.create(request);

        assertNotNull(result);

        verify(repository).existsByName("IT");
        verify(repository).save(any(DepartmentRecord.class));
    }

    @Test
    void shouldThrowExceptionWhenDepartmentNameExists() {

        CreateRequestRecordDto request =
                new CreateRequestRecordDto("IT", "Information Technology");

        when(repository.existsByName("IT")).thenReturn(true);

        assertThrows(
                DepartmentDuplicateNameCustomException.class,
                () -> service.create(request)
        );

        verify(repository).existsByName("IT");
        verify(repository, never()).save(any());
    }
}