package com.pfe.workflow.application.dto;

import com.pfe.workflow.domain.model.StepStatus;
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
public class SAGAStepDto {
    private UUID id;
    private String serviceName;
    private String action;
    private StepStatus status;
    private String compensationAction;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private String errorDetails;
}
