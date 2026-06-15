package com.example.erp.departmentservice.service.Services;

import com.example.erp.departmentservice.service.Contracts.IUserClientService;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.UUID;

@Service
public class UserClientService implements IUserClientService {
    private final WebClient webClient;

    public UserClientService(WebClient webClient) {
        this.webClient = webClient;
    }


    public boolean isUserExists(UUID userId) {
        try {
            return Boolean.TRUE.equals(webClient.get()
                    .uri("http://identity-service/api/users/{id}/getUser", userId)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block());
        } catch (Exception e) {
            return false;
        }
    }

    public boolean UpdateUserRole(UUID userId)
    {
        try {
            return Boolean.TRUE.equals(webClient.put()
                    .uri("http://identity-service/api/users/{id}/updateRoles", userId,"DEPARTMENT_MANAGER")
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block());
        } catch (Exception e) {
            return false;
        }
    }
}
