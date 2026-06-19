package com.example.erp.employeeservice.dto;

import com.example.erp.employeeservice.domain.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;
import java.util.UUID;

public class CreateEmployeeRequestDto
{
    @NotNull
    private UUID user_id;
    @NotBlank
    private String employee_number;
    @NotBlank
    private String first_name;
    @NotBlank
    private String last_name;
    @NotBlank
    private String email;
    @NotNull
    private UUID department_id;
    @NotNull
    private UUID manager_employee_id;
    @NotNull
    private OffsetDateTime hire_date;
    @NotNull
    private Status status =  Status.ACTIVE;
    private Integer version = 1;
    public CreateEmployeeRequestDto(UUID user_id, String employee_number,
                                    String first_name, String last_name,
                                    String email, UUID department_id,
                                    UUID manager_employee_id, OffsetDateTime hire_date,
                                    Status status,Integer version)
    {
        this.user_id = user_id;
        this.employee_number = employee_number;
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.department_id = department_id;
        this.manager_employee_id = manager_employee_id;
        this.hire_date = hire_date;
        this.status = status;
        this.version = version;
    }


    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public CreateEmployeeRequestDto() {
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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
