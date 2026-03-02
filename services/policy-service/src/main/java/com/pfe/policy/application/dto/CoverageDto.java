package com.pfe.policy.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoverageDto {
    private String id;
    private String coverageType;
    private BigDecimal amount;
    private BigDecimal deductible;
}
