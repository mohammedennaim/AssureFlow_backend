package com.pfe.notification.infrastructure.web.controller;

import com.pfe.commons.dto.BaseResponse;
import com.pfe.notification.application.dto.NotificationLogDto;
import com.pfe.notification.application.service.NotificationLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications/logs")
@RequiredArgsConstructor
@Tag(name = "Notification Logs", description = "Notification log read APIs")
public class NotificationLogController {

    private final NotificationLogService logService;

    @GetMapping("/{id}")
    @Operation(summary = "Get log by ID")
    public ResponseEntity<BaseResponse<NotificationLogDto>> getLog(@PathVariable UUID id) {
        NotificationLogDto dto = logService.getLogById(id);
        return ResponseEntity.ok(BaseResponse.success(dto));
    }

    @GetMapping("/notification/{notificationId}")
    @Operation(summary = "Get logs by notification ID")
    public ResponseEntity<BaseResponse<List<NotificationLogDto>>> getByNotificationId(
            @PathVariable UUID notificationId) {
        List<NotificationLogDto> list = logService.getLogsByNotificationId(notificationId);
        return ResponseEntity.ok(BaseResponse.success(list));
    }

    @GetMapping
    @Operation(summary = "Get all logs")
    public ResponseEntity<BaseResponse<List<NotificationLogDto>>> getAllLogs() {
        List<NotificationLogDto> list = logService.getAllLogs();
        return ResponseEntity.ok(BaseResponse.success(list));
    }
}
