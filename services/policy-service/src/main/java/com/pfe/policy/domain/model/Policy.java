package com.pfe.policy.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private BigDecimal premium;
    private String description;
    @Builder.Default
    private List<Coverage> coverages = new ArrayList<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public void cancel() {
        this.status = PolicyStatus.CANCELLED;
    }

    public void activate() {
        this.status = PolicyStatus.ACTIVE;
    }

    public boolean isActive() {
        return this.status == PolicyStatus.ACTIVE;
    }
}
