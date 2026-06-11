package com.example.erp.departmentservice.controller;

import com.example.erp.common.api.ApiResponse;
import com.example.erp.departmentservice.dto.*;
import com.example.erp.departmentservice.service.DepartmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Validated
@RestController
@RequestMapping("/api/department")
public class DepartmentController
{
    private final DepartmentService departmentRecordService;


    public DepartmentController(DepartmentService departmentRecordService) {
        this.departmentRecordService = departmentRecordService;
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<CreateDepartmentResponseDto>> create(
            @Valid @RequestBody CreateDepartmentRequestDto request)
    {
        var result = departmentRecordService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(result));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UpdateDepartmentResponseDto>> update(
            @PathVariable UUID id,
            @RequestBody UpdateDepartmentRequestDto request)
    {
        request.setId(id);

        var result = departmentRecordService.update(request);

        return ResponseEntity
                .ok(ApiResponse.success(result));
    }


    @GetMapping("/find/{id}")
    public ResponseEntity<ApiResponse<FindDepartmentResponseDto>> find(
            @PathVariable UUID id) {

        var result = departmentRecordService.findById(id);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(result));
    }


    @GetMapping("/findall")
    public ResponseEntity<ApiResponse<List<FindDepartmentResponseDto>>> findAll() {
        var result = departmentRecordService.findAll();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(result));
    }

}
