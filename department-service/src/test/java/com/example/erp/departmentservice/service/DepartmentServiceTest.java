package com.example.erp.departmentservice.service;

import com.example.erp.departmentservice.domain.Department;
import com.example.erp.departmentservice.dto.*;
import com.example.erp.departmentservice.exceptions.DepartmentDuplicateNameCustomException;
import com.example.erp.departmentservice.exceptions.DepartmentFindByIdNullCustomException;
import com.example.erp.departmentservice.repository.DepartmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DepartmentServiceTest {

    @Mock
    private DepartmentRepository repository;

    @InjectMocks
    private DepartmentService service;

    @Test
    void shouldCreateDepartment() {

        CreateDepartmentRequestDto request =
                new CreateDepartmentRequestDto(
                        "IT",
                        "Information Technology"
                );

        Department savedEntity = new Department();
        savedEntity.setId(UUID.randomUUID());
        savedEntity.setName("IT");
        savedEntity.setDescription("Information Technology");

        when(repository.save(any(Department.class)))
                .thenReturn(savedEntity);

        CreateDepartmentResponseDto result = service.create(request);

        assertNotNull(result);

        verify(repository).save(any(Department.class));
    }

    @Test
    void shouldCreateDepartmentWhenNameDoesNotExist() {

        CreateDepartmentRequestDto request =
                new CreateDepartmentRequestDto("IT", "Information Technology");

        Department saved = new Department();
        saved.setId(UUID.randomUUID());
        saved.setName("IT");

        when(repository.existsByName("IT")).thenReturn(false);
        when(repository.save(any(Department.class))).thenReturn(saved);

        CreateDepartmentResponseDto result = service.create(request);

        assertNotNull(result);

        verify(repository).existsByName("IT");
        verify(repository).save(any(Department.class));
    }

    @Test
    void shouldThrowExceptionWhenDepartmentNameExists() {

        CreateDepartmentRequestDto request =
                new CreateDepartmentRequestDto("IT", "Information Technology");

        when(repository.existsByName("IT")).thenReturn(true);

        assertThrows(
                DepartmentDuplicateNameCustomException.class,
                () -> service.create(request)
        );

        verify(repository).existsByName("IT");
        verify(repository, never()).save(any());
    }

    @Test
    void update_ShouldReturnUpdatedDto_WhenRequestIsValid() {
        UUID id = UUID.randomUUID();
        UpdateDepartmentRequestDto requestDto = new UpdateDepartmentRequestDto(id, "New Name", "New Desc");

        Department existingEntity = new Department();
        existingEntity.setId(id);
        existingEntity.setName("Old Name");

        Department savedEntity = new Department();
        savedEntity.setId(id);
        savedEntity.setName("New Name");
        savedEntity.setDescription("New Desc");

        when(repository.findById(id)).thenReturn(Optional.of(existingEntity));

        when(repository.existsByName("New Name")).thenReturn(false);
        when(repository.save(any(Department.class))).thenReturn(savedEntity);

        UpdateDepartmentResponseDto result = service.update(requestDto);

        assertNotNull(result);
        assertEquals("New Name", result.getName());
        verify(repository).save(any(Department.class));
    }

    @Test
    void update_ShouldThrowNotFoundException_WhenIdDoesNotExist() {
        UUID id = UUID.randomUUID();
        UpdateDepartmentRequestDto requestDto = new UpdateDepartmentRequestDto(id, "Name", "Desc");

        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(DepartmentFindByIdNullCustomException.class, () -> {
            service.update(requestDto);
        });

        verify(repository, never()).save(any());
    }

    @Test
    void update_ShouldThrowDuplicateNameException_WhenNameAlreadyExistsForOtherId() {
        UUID id = UUID.randomUUID();
        UpdateDepartmentRequestDto requestDto = new UpdateDepartmentRequestDto(id, "DuplicateName", "Desc");

        Department existingEntity = new Department();
        existingEntity.setId(id);
        existingEntity.setName("Old Name");

        when(repository.findById(id)).thenReturn(Optional.of(existingEntity));
        when(repository.existsByName("DuplicateName")).thenReturn(true);

        assertThrows(DepartmentDuplicateNameCustomException.class, () -> {
            service.update(requestDto);
        });

        verify(repository, never()).save(any());
    }

    @Test
    void findById_ShouldReturnDto_WhenIdExists() {
        UUID id = UUID.randomUUID();
        Department entity = new Department();
        entity.setId(id);
        entity.setName("IT");
        entity.setDescription("Information Technology");

        when(repository.findById(id)).thenReturn(Optional.of(entity));

        FindDepartmentResponseDto result = service.findById(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals("IT", result.getName());
        assertEquals("Information Technology", result.getDescription());

        verify(repository).findById(id);
    }

    @Test
    void findById_ShouldThrowException_WhenIdDoesNotExist() {
        UUID id = UUID.randomUUID();

        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(DepartmentFindByIdNullCustomException.class, () -> {
            service.findById(id);
        });

        verify(repository).findById(id);
    }

    @Test
    void findAll_ShouldReturnList_WhenDataExists() {
        Department entity1 = new Department();
        entity1.setId(UUID.randomUUID());
        entity1.setName("IT");

        Department entity2 = new Department();
        entity2.setId(UUID.randomUUID());
        entity2.setName("HR");

        when(repository.findAll()).thenReturn(List.of(entity1, entity2));

        List<FindDepartmentResponseDto> result = service.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("IT", result.get(0).getName());
        assertEquals("HR", result.get(1).getName());

        verify(repository).findAll();
    }

    @Test
    void findAll_ShouldReturnEmptyList_WhenNoDataExists() {
        when(repository.findAll()).thenReturn(Collections.emptyList());

        List<FindDepartmentResponseDto> result = service.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(repository).findAll();
    }

}