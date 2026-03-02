package com.pfe.policy.application.dto;

import com.pfe.policy.domain.model.PolicyStatus;
import com.pfe.policy.domain.model.PolicyType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PolicyDto {
    private String id;
    private String policyNumber;
    private String clientId;
    private PolicyType type;
    private PolicyStatus status;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal premiumAmount;
    private List<CoverageDto> coverages;
    private List<BeneficiaryDto> beneficiaries;
    private List<PolicyDocumentDto> documents;
}
