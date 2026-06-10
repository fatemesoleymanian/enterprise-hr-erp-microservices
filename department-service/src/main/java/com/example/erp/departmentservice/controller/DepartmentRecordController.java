package com.example.erp.departmentservice.controller;

import com.example.erp.departmentservice.dto.CreateRequestRecordDto;
import com.example.erp.departmentservice.dto.CreateResponseRecordDto;
import com.example.erp.departmentservice.service.DepartmentRecordService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.erp.common.api.ApiResponse;

@Validated
@RestController
@RequestMapping("/api/department")
public class DepartmentRecordController
{
    private final DepartmentRecordService departmentRecordService;


    public DepartmentRecordController(DepartmentRecordService departmentRecordService) {
        this.departmentRecordService = departmentRecordService;
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<CreateResponseRecordDto>> create(
            @Valid @RequestBody CreateRequestRecordDto request)
    {
        var result = departmentRecordService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(result));
    }
}
