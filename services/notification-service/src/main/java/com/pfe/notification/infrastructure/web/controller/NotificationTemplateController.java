package com.pfe.notification.infrastructure.web.controller;

import com.pfe.commons.dto.BaseResponse;
import com.pfe.notification.application.dto.NotificationTemplateDto;
import com.pfe.notification.application.service.NotificationTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications/templates")
@RequiredArgsConstructor
@Tag(name = "Notification Templates", description = "Notification template management APIs")
public class NotificationTemplateController {

    private final NotificationTemplateService templateService;

    @PostMapping
    @Operation(summary = "Create a notification template")
    public ResponseEntity<BaseResponse<NotificationTemplateDto>> createTemplate(
            @RequestBody NotificationTemplateDto dto) {
        NotificationTemplateDto saved = templateService.createTemplate(dto);
        return ResponseEntity.ok(BaseResponse.success(saved, "Template created successfully"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get template by ID")
    public ResponseEntity<BaseResponse<NotificationTemplateDto>> getTemplate(@PathVariable UUID id) {
        NotificationTemplateDto dto = templateService.getTemplateById(id);
        return ResponseEntity.ok(BaseResponse.success(dto));
    }

    @GetMapping("/name/{name}")
    @Operation(summary = "Get template by name")
    public ResponseEntity<BaseResponse<NotificationTemplateDto>> getByName(@PathVariable String name) {
        NotificationTemplateDto dto = templateService.getTemplateByName(name);
        return ResponseEntity.ok(BaseResponse.success(dto));
    }

    @GetMapping
    @Operation(summary = "Get all templates")
    public ResponseEntity<BaseResponse<List<NotificationTemplateDto>>> getAllTemplates() {
        List<NotificationTemplateDto> list = templateService.getAllTemplates();
        return ResponseEntity.ok(BaseResponse.success(list));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a template")
    public ResponseEntity<BaseResponse<NotificationTemplateDto>> updateTemplate(
            @PathVariable UUID id, @RequestBody NotificationTemplateDto dto) {
        NotificationTemplateDto updated = templateService.updateTemplate(id, dto);
        return ResponseEntity.ok(BaseResponse.success(updated, "Template updated successfully"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a template")
    public ResponseEntity<BaseResponse<Void>> deleteTemplate(@PathVariable UUID id) {
        templateService.deleteTemplate(id);
        return ResponseEntity.ok(BaseResponse.success(null, "Template deleted successfully"));
    }
}
