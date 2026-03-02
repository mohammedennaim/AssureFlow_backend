package com.pfe.claims.domain.model;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Claim {
    private String id;
    private String claimNumber;
    private String policyId;
    private String clientId;
    private String description;
    private ClaimStatus status;
    private LocalDate incidentDate;
    private BigDecimal claimedAmount;
    private BigDecimal approvedAmount;
    @Builder.Default
    private List<ClaimDocument> documents = new ArrayList<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public void approve(BigDecimal approvedAmount) {
        this.status = ClaimStatus.APPROVED;
        this.approvedAmount = approvedAmount;
    }

    public void reject() {
        this.status = ClaimStatus.REJECTED;
    }

    public void startReview() {
        this.status = ClaimStatus.UNDER_REVIEW;
    }
}
