package com.pfe.claims.application.dto;

import com.pfe.claims.domain.model.AssessmentDecision;
import com.pfe.claims.domain.model.PrescriptionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimAssessmentDto {
    private UUID id;
    private UUID assessorId;
    private AssessmentDecision decision;
    private BigDecimal amount;
    private PrescriptionStatus status;
    private String notes;
}
