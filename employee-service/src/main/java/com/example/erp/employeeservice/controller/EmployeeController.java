package com.example.erp.employeeservice.controller;

import com.example.erp.common.api.ApiResponse;
import com.example.erp.employeeservice.dto.CreateEmployeeRequestDto;
import com.example.erp.employeeservice.dto.CreateEmployeeResponseDto;
import com.example.erp.employeeservice.dto.FindEmployeeResponseDto;
import com.example.erp.employeeservice.repository.IEmployeeRepository;
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
}
