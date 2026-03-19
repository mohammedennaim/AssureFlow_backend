package com.pfe.workflow.application.dto;

import com.pfe.workflow.domain.model.AuditAction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogDto {
    private UUID id;
    private UUID userId;
    private String username;
    private AuditAction action;
    private String entityType;
    private UUID entityId;
    private String oldValue;
    private String newValue;
    private String reason;
    private LocalDateTime timestamp;
    private String ipAddress;
    private String userAgent;
}
