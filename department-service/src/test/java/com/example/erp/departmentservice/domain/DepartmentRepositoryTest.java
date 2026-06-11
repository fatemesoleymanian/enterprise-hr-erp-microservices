package com.example.erp.departmentservice.domain;

import com.example.erp.departmentservice.repository.DepartmentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
class DepartmentRepositoryTest {

    @Autowired
    private DepartmentRepository repository;

    @Test
    void shouldSaveDepartment() {

        Department department = new Department();

        department.setName("IT");
        department.setManagerUserId(UUID.randomUUID());
        department.setCreatedAt(OffsetDateTime.now());
        department.setUpdatedAt(OffsetDateTime.now());

        Department saved = repository.save(department);

        assertNotNull(saved.getId());
    }
}
