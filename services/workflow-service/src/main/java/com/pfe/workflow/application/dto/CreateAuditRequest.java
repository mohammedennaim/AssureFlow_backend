package com.pfe.workflow.application.dto;

import com.pfe.workflow.domain.model.AuditAction;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAuditRequest {
    
    @NotNull(message = "User ID is required")
    private UUID userId;
    
    @NotNull(message = "Username is required")
    private String username;
    
    @NotNull(message = "Action is required")
    private AuditAction action;
    
    @NotNull(message = "Entity type is required")
    private String entityType;
    
    @NotNull(message = "Entity ID is required")
    private UUID entityId;
    
    private String oldValue;
    private String newValue;
    private String reason;
    private String ipAddress;
    private String userAgent;
}
