package com.example.erp.notification_service.web;

import com.example.erp.common.api.ApiResponse;
import com.example.erp.notification_service.domain.NotificationType;
import com.example.erp.notification_service.service.NotificationService;
import com.example.erp.notification_service.web.dto.NotificationResponse;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Validated
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public ApiResponse<List<NotificationResponse>> notifications(
            @RequestParam(required = false) String recipientRole,
            @RequestParam(required = false) UUID recipientUserId,
            @RequestParam(required = false) Boolean read,
            @RequestParam(required = false) NotificationType type
    ) {
        return ApiResponse.success(notificationService
                .findNotifications(recipientRole, recipientUserId, read, type)
                .stream()
                .map(NotificationResponse::from)
                .toList());
    }

    @PatchMapping("/{id}/read")
    public ApiResponse<NotificationResponse> markRead(@PathVariable UUID id) {
        return ApiResponse.success(NotificationResponse.from(notificationService.markRead(id)));
    }
}
