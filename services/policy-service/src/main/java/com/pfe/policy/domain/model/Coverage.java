package com.pfe.policy.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Coverage {
    private String id;
    private String policyId;
    private String coverageType;
    private BigDecimal amount;
    private BigDecimal deductible;
}
