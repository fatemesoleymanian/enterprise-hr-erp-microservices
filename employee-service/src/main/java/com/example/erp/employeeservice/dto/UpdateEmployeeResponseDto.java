package com.example.erp.employeeservice.dto;

import com.example.erp.employeeservice.domain.Status;

import java.time.OffsetDateTime;
import java.util.UUID;

public class UpdateEmployeeResponseDto
{
    private UUID id;
    private UUID user_id;
    private String employee_number;
    private String first_name;
    private String last_name;
    private String email;
    private UUID department_id;
    private UUID manager_employee_id;
    private OffsetDateTime hire_date;
    private OffsetDateTime created_at;
    private OffsetDateTime updated_at;
    private Integer version;
    private Status status;


    public UpdateEmployeeResponseDto(UUID id, UUID user_id,
                                    String employee_number, String first_name,
                                    String last_name, String email,
                                    UUID department_id, UUID manager_employee_id,
                                    OffsetDateTime hire_date, OffsetDateTime created_at,
                                    OffsetDateTime updated_at, Integer version,
                                    Status status)
    {
        this.id = id;
        this.user_id = user_id;
        this.employee_number = employee_number;
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.department_id = department_id;
        this.manager_employee_id = manager_employee_id;
        this.hire_date = hire_date;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.version = version;
        this.status = status;
    }

    public UpdateEmployeeResponseDto() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUser_id() {
        return user_id;
    }

    public void setUser_id(UUID user_id) {
        this.user_id = user_id;
    }

    public String getEmployee_number() {
        return employee_number;
    }

    public void setEmployee_number(String employee_number) {
        this.employee_number = employee_number;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UUID getDepartment_id() {
        return department_id;
    }

    public void setDepartment_id(UUID department_id) {
        this.department_id = department_id;
    }

    public UUID getManager_employee_id() {
        return manager_employee_id;
    }

    public void setManager_employee_id(UUID manager_employee_id) {
        this.manager_employee_id = manager_employee_id;
    }

    public OffsetDateTime getHire_date() {
        return hire_date;
    }

    public void setHire_date(OffsetDateTime hire_date) {
        this.hire_date = hire_date;
    }

    public OffsetDateTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(OffsetDateTime created_at) {
        this.created_at = created_at;
    }

    public OffsetDateTime getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(OffsetDateTime updated_at) {
        this.updated_at = updated_at;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

}

