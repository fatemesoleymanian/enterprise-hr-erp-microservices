package com.example.erp.departmentservice.repository;

import com.example.erp.departmentservice.domain.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface IDepartmentRepository extends JpaRepository<Department, UUID>
{

    boolean existsByName(String name);
}
