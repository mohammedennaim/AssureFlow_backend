package com.pfe.workflow.application.dto;

import com.pfe.workflow.domain.model.SAGAStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SAGATransactionDto {
    private UUID id;
    private String sagaType;
    private SAGAStatus status;
    private UUID initiatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<SAGAStepDto> steps;
}
