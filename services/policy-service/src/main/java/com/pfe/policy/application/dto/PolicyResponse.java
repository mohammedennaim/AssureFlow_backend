package com.pfe.policy.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PolicyResponse {
    private String id;
    private String policyNumber;
    private String clientId;
    private String type;
    private String status;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal premium;
    private String description;
    private List<CoverageResponse> coverages;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
