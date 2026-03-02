package com.pfe.policy.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Policy {
    private String id;
    private String policyNumber;
    private String clientId;
    private PolicyType type;
    private PolicyStatus status;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal premiumAmount;

    @Builder.Default
    private List<Coverage> coverages = new ArrayList<>();

    @Builder.Default
    private List<Beneficiary> beneficiaries = new ArrayList<>();

    @Builder.Default
    private List<PolicyDocument> documents = new ArrayList<>();

    // Methods mentioned in diagram: calculatePremium, renewPolicy, cancelPolicy
    public BigDecimal calculatePremium() {
        // Business logic to be implemented
        return this.premiumAmount;
    }

    public void renewPolicy(Policy newPolicy) {
        // Business logic to be implemented
    }

    public void cancelPolicy() {
        this.status = PolicyStatus.CANCELLED;
    }
}
