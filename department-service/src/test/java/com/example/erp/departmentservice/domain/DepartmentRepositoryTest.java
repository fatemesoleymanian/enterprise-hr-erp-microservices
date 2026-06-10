package com.example.erp.departmentservice.domain;

import com.example.erp.departmentservice.repository.DepartmentRecordRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
class DepartmentRepositoryTest {

    @Autowired
    private DepartmentRecordRepository repository;

    @Test
    void shouldSaveDepartment() {

        DepartmentRecord department = new DepartmentRecord();

        department.setName("IT");
        department.setManagerUserId(UUID.randomUUID());
        department.setCreatedAt(OffsetDateTime.now());
        department.setUpdatedAt(OffsetDateTime.now());

        DepartmentRecord saved = repository.save(department);

        assertNotNull(saved.getId());
    }
}
