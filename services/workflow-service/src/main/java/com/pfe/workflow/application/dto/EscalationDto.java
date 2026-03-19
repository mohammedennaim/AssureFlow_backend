package com.pfe.workflow.application.dto;

import com.pfe.workflow.domain.model.EscalationLevel;
import com.pfe.workflow.domain.model.EscalationStatus;
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
public class EscalationDto {
    private UUID id;
    private UUID entityId;
    private String entityType;
    private EscalationLevel level;
    private EscalationStatus status;
    private String reason;
    private String description;
    private UUID assignedTo;
    private String assignedToName;
    private UUID slaViolationId;
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;
    private UUID resolvedBy;
    private String resolution;
}
