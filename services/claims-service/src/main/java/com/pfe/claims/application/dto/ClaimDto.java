package com.pfe.claims.application.dto;

import com.pfe.claims.domain.model.ClaimStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimDto {
    private UUID id;
    private String claimNumber;
    private UUID policyId;
    private UUID clientId;
    private ClaimStatus status;
    private LocalDate incidentDate;
    private String description;
    private BigDecimal estimatedAmount;
    private BigDecimal approvedAmount;
    private UUID submittedBy;
    private UUID approvedBy;
    private UUID assignedTo;
    private LocalDateTime createdAt;
    private List<ClaimDocumentDto> documents;
    private List<ClaimAssessmentDto> assessments;
    private PayoutDto payout;
}
