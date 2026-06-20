package com.example.erp.employeeservice.domain;

import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeTest {

    @Test
    void shouldCreateEmployeeAndGetValues() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID departmentId = UUID.randomUUID();
        UUID managerId = UUID.randomUUID();

        OffsetDateTime now = OffsetDateTime.now();

        Employee employee = new Employee(
                id,
                userId,
                "Ali",
                "EMP-001",
                "Ahmadi",
                "ali@example.com",
                departmentId,
                managerId,
                now,
                now,
                now,
                1,
                Status.ACTIVE
        );

        assertEquals(id, employee.getId());
        assertEquals(userId, employee.getUser_id());
        assertEquals("EMP-001", employee.getEmployee_number());
        assertEquals("Ali", employee.getFirst_name());
        assertEquals("Ahmadi", employee.getLast_name());
        assertEquals("ali@example.com", employee.getEmail());
        assertEquals(departmentId, employee.getDepartment_id());
        assertEquals(managerId, employee.getManager_employee_id());
        assertEquals(now, employee.getHire_date());
        assertEquals(1, employee.getVersion());
        assertEquals(Status.ACTIVE, employee.getStatus());
    }
}

