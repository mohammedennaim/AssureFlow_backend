package com.pfe.workflow.application.dto;

import com.pfe.workflow.domain.model.SLAStatus;
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
public class SLAViolationDto {
    private UUID id;
    private UUID slaDefinitionId;
    private String slaDefinitionName;
    private UUID entityId;
    private String entityType;
    private LocalDateTime deadline;
    private LocalDateTime violatedAt;
    private Long delayMinutes;
    private SLAStatus status;
    private Boolean escalated;
    private UUID escalationId;
    private String notes;
    private LocalDateTime createdAt;
}
