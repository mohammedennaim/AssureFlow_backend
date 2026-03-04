package com.pfe.policy.domain.model;

import com.pfe.commons.annotations.AggregateRoot;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@AggregateRoot
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
    private BigDecimal coverageAmount;
    private LocalDateTime createdAt;

    @Builder.Default
    private List<Coverage> coverages = new ArrayList<>();

    @Builder.Default
    private List<Beneficiary> beneficiaries = new ArrayList<>();

    @Builder.Default
    private List<PolicyDocument> documents = new ArrayList<>();

    @Builder.Default
    private transient List<Object> domainEvents = new ArrayList<>();

    public void registerEvent(Object event) {
        if (this.domainEvents == null) {
            this.domainEvents = new ArrayList<>();
        }
        this.domainEvents.add(event);
    }

    public List<Object> getDomainEvents() {
        if (this.domainEvents == null) {
            return new ArrayList<>();
        }
        return java.util.Collections.unmodifiableList(this.domainEvents);
    }

    public void clearDomainEvents() {
        if (this.domainEvents != null) {
            this.domainEvents.clear();
        }
    }

    public BigDecimal calculatePremium() {
        if (coverageAmount != null && coverageAmount.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal rate = switch (type) {
                case HEALTH -> new BigDecimal("0.05");
                case LIFE -> new BigDecimal("0.03");
                case VEHICLE -> new BigDecimal("0.07");
                case HOME -> new BigDecimal("0.04");
                case BUSINESS -> new BigDecimal("0.06");
            };
            this.premiumAmount = coverageAmount.multiply(rate);
        }
        return this.premiumAmount;
    }

    public Policy evaluatePolicy() {
        calculatePremium();
        return this;
    }

    public void submit() {
        if (this.status != PolicyStatus.DRAFT) {
            throw new IllegalStateException("Only DRAFT policies can be submitted. Current status: " + this.status);
        }
        this.status = PolicyStatus.ACTIVE;
    }

    public void approve() {
        if (this.status != PolicyStatus.ACTIVE) {
            throw new IllegalStateException("Only ACTIVE policies can be approved. Current status: " + this.status);
        }
    }

    public void reject(String reason) {
        if (this.status == PolicyStatus.CANCELLED || this.status == PolicyStatus.EXPIRED) {
            throw new IllegalStateException("Cannot reject a policy with status: " + this.status);
        }
        this.status = PolicyStatus.CANCELLED;
    }

    public void cancel(String reason) {
        if (this.status == PolicyStatus.CANCELLED) {
            throw new IllegalStateException("Policy is already cancelled.");
        }
        this.status = PolicyStatus.CANCELLED;
    }

    public void expire(String reason) {
        if (this.status != PolicyStatus.ACTIVE) {
            throw new IllegalStateException("Only ACTIVE policies can expire. Current status: " + this.status);
        }
        this.status = PolicyStatus.EXPIRED;
    }

    public void renewPolicy(Policy newPolicy) {
        if (this.status != PolicyStatus.ACTIVE && this.status != PolicyStatus.EXPIRED) {
            throw new IllegalStateException(
                    "Only ACTIVE or EXPIRED policies can be renewed. Current status: " + this.status);
        }
    }
}
