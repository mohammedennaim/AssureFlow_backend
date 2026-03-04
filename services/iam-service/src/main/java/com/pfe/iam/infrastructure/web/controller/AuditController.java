package com.pfe.iam.infrastructure.web.controller;

import com.pfe.commons.dto.BaseResponse;
import com.pfe.iam.application.dto.AuditLogDto;
import com.pfe.iam.application.service.AuditService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/audit")
@RequiredArgsConstructor
@Tag(name = "Audit", description = "Audit log APIs")
@PreAuthorize("hasRole('ADMIN')")
public class AuditController {

    private final AuditService auditService;

    @GetMapping
    @Operation(summary = "Get all audit logs")
    public ResponseEntity<BaseResponse<List<AuditLogDto>>> getAllAuditLogs() {
        List<AuditLogDto> logs = auditService.getAllAuditLogs();
        return ResponseEntity.ok(BaseResponse.success(logs, "Audit logs retrieved successfully"));
    }

    @GetMapping("/users/{userId}")
    @Operation(summary = "Get audit logs for a specific user")
    public ResponseEntity<BaseResponse<List<AuditLogDto>>> getUserAuditLogs(@PathVariable String userId) {
        List<AuditLogDto> logs = auditService.getAuditLogsByUserId(userId);
        return ResponseEntity.ok(BaseResponse.success(logs, "User audit logs retrieved successfully"));
    }
}
