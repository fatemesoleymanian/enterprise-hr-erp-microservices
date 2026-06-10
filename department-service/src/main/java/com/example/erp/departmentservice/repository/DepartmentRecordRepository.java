package com.example.erp.departmentservice.repository;

import com.example.erp.departmentservice.domain.DepartmentRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DepartmentRecordRepository extends JpaRepository<DepartmentRecord, UUID>
{

    boolean existsByName(String name);
}
