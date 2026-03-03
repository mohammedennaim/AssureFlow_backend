package com.pfe.claims.domain.model;

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
public class ClaimAssessment {
    private UUID id;
    private UUID claimId;
    private UUID assessorId;
    private AssessmentDecision decision;
    private BigDecimal amount;
    private PrescriptionStatus status;
    private String notes;
}
