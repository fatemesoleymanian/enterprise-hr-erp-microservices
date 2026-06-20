package com.example.erp.employeeservice.service.services;

import com.example.erp.employeeservice.service.contracts.IDepartmentClientService;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.UUID;

public class DepartmentClientService implements IDepartmentClientService
{
    private final WebClient webClient;

    public DepartmentClientService(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public boolean isDepartmentExists(UUID department_id) {
        try
        {
            return Boolean.TRUE.equals(webClient.get()
                    .uri("http://department-service/api/department/{id}/findById", department_id)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block());
        }
        catch (Exception e)
        {
            return false;
        }
    }
}
