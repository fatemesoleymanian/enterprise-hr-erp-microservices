package com.example.erp.departmentservice.dto;

import jakarta.validation.constraints.NotBlank;

public class CreateRequestRecordDto
{
    @NotBlank
    private String name;
    @NotBlank
    private String description;

    public CreateRequestRecordDto() {}

    public CreateRequestRecordDto( String name, String description)

    {
        this.name = name;
        this.description = description;
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


}
