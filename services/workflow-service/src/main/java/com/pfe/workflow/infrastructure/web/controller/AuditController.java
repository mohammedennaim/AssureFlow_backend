package com.pfe.workflow.infrastructure.web.controller;

import com.pfe.workflow.application.dto.AuditLogDto;
import com.pfe.workflow.application.dto.CreateAuditRequest;
import com.pfe.workflow.application.service.AuditService;
import com.pfe.workflow.domain.model.AuditAction;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/audit")
@RequiredArgsConstructor
public class AuditController {

    private final AuditService auditService;

    @PostMapping
    public ResponseEntity<AuditLogDto> createAuditLog(@Valid @RequestBody CreateAuditRequest request) {
        AuditLogDto created = auditService.createAuditLog(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<Page<AuditLogDto>> getAllAuditLogs(Pageable pageable) {
        Page<AuditLogDto> auditLogs = auditService.getAllAuditLogs(pageable);
        return ResponseEntity.ok(auditLogs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuditLogDto> getAuditLogById(@PathVariable UUID id) {
        AuditLogDto auditLog = auditService.getAuditLogById(id);
        return ResponseEntity.ok(auditLog);
    }

    @GetMapping("/entity/{entityType}/{entityId}")
    public ResponseEntity<Page<AuditLogDto>> getAuditLogsByEntity(
            @PathVariable String entityType,
            @PathVariable UUID entityId,
            Pageable pageable) {
        Page<AuditLogDto> auditLogs = auditService.getAuditLogsByEntity(entityType, entityId, pageable);
        return ResponseEntity.ok(auditLogs);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<AuditLogDto>> getAuditLogsByUser(
            @PathVariable UUID userId,
            Pageable pageable) {
        Page<AuditLogDto> auditLogs = auditService.getAuditLogsByUser(userId, pageable);
        return ResponseEntity.ok(auditLogs);
    }

    @GetMapping("/action/{action}")
    public ResponseEntity<Page<AuditLogDto>> getAuditLogsByAction(
            @PathVariable AuditAction action,
            Pageable pageable) {
        Page<AuditLogDto> auditLogs = auditService.getAuditLogsByAction(action, pageable);
        return ResponseEntity.ok(auditLogs);
    }

    @GetMapping("/entity-type/{entityType}")
    public ResponseEntity<Page<AuditLogDto>> getAuditLogsByEntityType(
            @PathVariable String entityType,
            Pageable pageable) {
        Page<AuditLogDto> auditLogs = auditService.getAuditLogsByEntityType(entityType, pageable);
        return ResponseEntity.ok(auditLogs);
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<AuditLogDto>> getAuditLogsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        List<AuditLogDto> auditLogs = auditService.getAuditLogsByDateRange(start, end);
        return ResponseEntity.ok(auditLogs);
    }
}
