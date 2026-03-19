package com.pfe.workflow.application.dto;

import com.pfe.workflow.domain.model.EscalationLevel;
import jakarta.validation.constraints.NotBlank;
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
public class CreateEscalationRequest {
    
    @NotNull(message = "Entity ID is required")
    private UUID entityId;
    
    @NotBlank(message = "Entity type is required")
    private String entityType;
    
    @NotNull(message = "Escalation level is required")
    private EscalationLevel level;
    
    @NotBlank(message = "Reason is required")
    private String reason;
    
    private String description;
    private UUID assignedTo;
    private String assignedToName;
    private UUID slaViolationId;
}
