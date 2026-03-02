package com.pfe.claims.application.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimResponse {
    private String id;
    private String claimNumber;
    private String policyId;
    private String clientId;
    private String description;
    private String status;
    private LocalDate incidentDate;
    private BigDecimal claimedAmount;
    private BigDecimal approvedAmount;
    private List<ClaimDocumentResponse> documents;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
