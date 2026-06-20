package com.example.erp.employeeservice.controller;

import com.example.erp.common.api.ApiResponse;
import com.example.erp.employeeservice.domain.Status;
import com.example.erp.employeeservice.dto.*;
import com.example.erp.employeeservice.service.contracts.IEmployeeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.UUID;

@Validated
@RestController
@RequestMapping("/api/employee")
public class EmployeeController
{
    private final IEmployeeService employeeService;

    public EmployeeController(IEmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN','HR_MANAGER')")
    public ResponseEntity<ApiResponse<CreateEmployeeResponseDto>> create(
            @Valid @RequestBody CreateEmployeeRequestDto request)
    {
        var result = employeeService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(result));
    }

    @GetMapping("/findById")
    public ResponseEntity<ApiResponse<FindEmployeeResponseDto>> findById(@RequestParam UUID id)
    {
        var result = employeeService.findById(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(result));
    }

    @GetMapping("/findAll")
    public ResponseEntity<ApiResponse<ArrayList<FindEmployeeResponseDto>>> findAll()
    {
        var result = employeeService.findAll();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(result));
    }

    @PutMapping("/update{id}")
    @PreAuthorize("hasAnyRole('ADMIN','HR_MANAGER')")
    public ResponseEntity<ApiResponse<UpdateEmployeeResponseDto>> update(@PathVariable UUID id,
                                                                         @RequestBody  UpdateEmployeeRequestDto  request)
    {
        var result = employeeService.update(id, request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(result));
    }

    @PatchMapping("/update-status{id}")
    @PreAuthorize("hasAnyRole('ADMIN','HR_MANAGER')")
    public ResponseEntity<ApiResponse<UpdateStatusEmployeeResponseDto>> updateStatus(@PathVariable UUID id, @RequestBody Status status)
    {
        var result = employeeService.updateStatus(id, status);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(result));
    }

    @PatchMapping("/update-department/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','HR_MANAGER')")
    public ResponseEntity<ApiResponse<UpdateDepartmentEmployeeResponseDto>> updateDepartment(
            @PathVariable("id") UUID id,
            @RequestBody UUID department_id
    ) {
        var result = employeeService.updateDepartment(id, department_id);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(result));
    }



}
