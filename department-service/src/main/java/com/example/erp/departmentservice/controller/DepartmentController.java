package com.example.erp.departmentservice.controller;

import com.example.erp.common.api.ApiResponse;
import com.example.erp.departmentservice.dto.*;
import com.example.erp.departmentservice.service.Contracts.IDepartmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Validated
@RestController
@RequestMapping("/api/departments")
public class DepartmentController
{
    private final IDepartmentService departmentRecordService;


    public DepartmentController(IDepartmentService departmentRecordService) {
        this.departmentRecordService = departmentRecordService;
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN','HR_MANAGER')")
    public ResponseEntity<ApiResponse<CreateDepartmentResponseDto>> create(
            @Valid @RequestBody CreateDepartmentRequestDto request)
    {
        var result = departmentRecordService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(result));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','HR_MANAGER')")
    public ResponseEntity<ApiResponse<UpdateDepartmentResponseDto>> update(
            @PathVariable UUID id,
            @RequestBody UpdateDepartmentRequestDto request)
    {
        request.setId(id);

        var result = departmentRecordService.update(request);

        return ResponseEntity
                .ok(ApiResponse.success(result));
    }

    @PatchMapping("/{id}/manager")
    @PreAuthorize("hasAnyRole('ADMIN','HR_MANAGER')")
    public ResponseEntity<ApiResponse<AssignDepartmentUpdateResponseDto>> assignDepartment(
            @PathVariable UUID id,
            @RequestParam UUID managerId)
    {

        var result = departmentRecordService.assignDepartmentToManager(id,managerId);

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
