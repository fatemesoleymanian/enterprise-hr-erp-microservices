package com.example.erp.departmentservice.dto;

import java.util.UUID;

public class UpdateDepartmentRequestDto
{
    private UUID id;
    private String name;

    public void setId(UUID id) {
        this.id = id;
    }

    private String description;

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public UpdateDepartmentRequestDto(UUID id,String name, String description) {
        this.name = name;
        this.description = description;
        this.id = id;
    }

    public UpdateDepartmentRequestDto() {
    }
}
