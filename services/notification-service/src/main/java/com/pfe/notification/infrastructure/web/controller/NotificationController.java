package com.pfe.notification.infrastructure.web.controller;

import com.pfe.notification.application.dto.NotificationRequest;
import com.pfe.notification.application.dto.NotificationResponse;
import com.pfe.notification.application.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications API", description = "Endpoints for managing notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping
    @Operation(summary = "Send a notification")
    public ResponseEntity<NotificationResponse> sendNotification(@Valid @RequestBody NotificationRequest request) {
        return new ResponseEntity<>(notificationService.sendNotification(request), HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get all notifications")
    public ResponseEntity<List<NotificationResponse>> getAllNotifications() {
        return ResponseEntity.ok(notificationService.getAllNotifications());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get notification by ID")
    public ResponseEntity<NotificationResponse> getNotificationById(@PathVariable String id) {
        return ResponseEntity.ok(notificationService.getNotificationById(id));
    }

    @GetMapping("/recipient/{recipientId}")
    @Operation(summary = "Get notifications by recipient")
    public ResponseEntity<List<NotificationResponse>> getByRecipient(@PathVariable String recipientId) {
        return ResponseEntity.ok(notificationService.getNotificationsByRecipient(recipientId));
    }

    @GetMapping("/recipient/{recipientId}/unread")
    @Operation(summary = "Get unread notifications for a recipient")
    public ResponseEntity<List<NotificationResponse>> getUnreadByRecipient(@PathVariable String recipientId) {
        return ResponseEntity.ok(notificationService.getUnreadNotifications(recipientId));
    }

    @PatchMapping("/{id}/read")
    @Operation(summary = "Mark a notification as read")
    public ResponseEntity<NotificationResponse> markAsRead(@PathVariable String id) {
        return ResponseEntity.ok(notificationService.markAsRead(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a notification")
    public ResponseEntity<Void> deleteNotification(@PathVariable String id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }
}
