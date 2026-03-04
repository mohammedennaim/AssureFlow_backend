package com.pfe.notification.infrastructure.web.controller;

import com.pfe.commons.dto.BaseResponse;
import com.pfe.notification.application.dto.CreateNotificationRequest;
import com.pfe.notification.application.dto.NotificationDto;
import com.pfe.notification.application.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "Notification management APIs")
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping
    @Operation(summary = "Create a notification")
    public ResponseEntity<BaseResponse<NotificationDto>> createNotification(
            @Valid @RequestBody CreateNotificationRequest request) {
        NotificationDto dto = notificationService.createNotification(request);
        return ResponseEntity.ok(BaseResponse.success(dto, "Notification created successfully"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get notification by ID")
    public ResponseEntity<BaseResponse<NotificationDto>> getNotification(@PathVariable UUID id) {
        NotificationDto dto = notificationService.getNotificationById(id);
        return ResponseEntity.ok(BaseResponse.success(dto));
    }

    @GetMapping("/policy/{policyId}")
    @Operation(summary = "Get notifications by policy ID")
    public ResponseEntity<BaseResponse<List<NotificationDto>>> getByPolicyId(@PathVariable UUID policyId) {
        List<NotificationDto> list = notificationService.getNotificationsByPolicyId(policyId);
        return ResponseEntity.ok(BaseResponse.success(list));
    }

    @GetMapping("/recipient/{recipient}")
    @Operation(summary = "Get notifications by recipient")
    public ResponseEntity<BaseResponse<List<NotificationDto>>> getByRecipient(@PathVariable String recipient) {
        List<NotificationDto> list = notificationService.getNotificationsByRecipient(recipient);
        return ResponseEntity.ok(BaseResponse.success(list));
    }

    @GetMapping
    @Operation(summary = "Get all notifications")
    public ResponseEntity<BaseResponse<List<NotificationDto>>> getAllNotifications() {
        List<NotificationDto> list = notificationService.getAllNotifications();
        return ResponseEntity.ok(BaseResponse.success(list));
    }

    @PostMapping("/{id}/send")
    @Operation(summary = "Send a notification")
    public ResponseEntity<BaseResponse<Void>> sendNotification(@PathVariable UUID id) {
        notificationService.sendNotification(id);
        return ResponseEntity.ok(BaseResponse.success(null, "Notification sent successfully"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a notification")
    public ResponseEntity<BaseResponse<Void>> deleteNotification(@PathVariable UUID id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.ok(BaseResponse.success(null, "Notification deleted successfully"));
    }
}
