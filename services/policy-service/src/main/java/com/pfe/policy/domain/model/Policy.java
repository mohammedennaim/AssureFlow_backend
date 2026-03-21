package com.pfe.policy.domain.model;

import com.pfe.commons.annotations.AggregateRoot;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.NoArgsConstructor;
import com.pfe.commons.events.DomainEvent;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@AggregateRoot
@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Policy {

    public static final BigDecimal HEALTH_RATE = new BigDecimal("0.05");
    public static final BigDecimal LIFE_RATE = new BigDecimal("0.03");
    public static final BigDecimal VEHICLE_RATE = new BigDecimal("0.07");
    public static final BigDecimal HOME_RATE = new BigDecimal("0.04");
    public static final BigDecimal BUSINESS_RATE = new BigDecimal("0.06");
    public static final BigDecimal MIN_COVERAGE_AMOUNT = new BigDecimal("100");
    public static final BigDecimal MAX_COVERAGE_AMOUNT = new BigDecimal("10000000");

    @EqualsAndHashCode.Include
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
    private transient List<DomainEvent> domainEvents = new ArrayList<>();

    public void registerEvent(DomainEvent event) {
        if (this.domainEvents == null) {
            this.domainEvents = new ArrayList<>();
        }
        this.domainEvents.add(event);
    }

    public List<DomainEvent> getDomainEvents() {
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

    public void validateCoverageAmount() {
        if (coverageAmount == null) {
            throw new IllegalArgumentException("Coverage amount is required.");
        }
        if (coverageAmount.compareTo(MIN_COVERAGE_AMOUNT) < 0) {
            throw new IllegalArgumentException(
                    "Coverage amount must be at least " + MIN_COVERAGE_AMOUNT + ". Got: " + coverageAmount);
        }
        if (coverageAmount.compareTo(MAX_COVERAGE_AMOUNT) > 0) {
            throw new IllegalArgumentException(
                    "Coverage amount cannot exceed " + MAX_COVERAGE_AMOUNT + ". Got: " + coverageAmount);
        }
    }

    public void validateDates() {
        if (startDate == null) {
            throw new IllegalArgumentException("Start date is required.");
        }
        if (endDate == null) {
            throw new IllegalArgumentException("End date is required.");
        }
        if (!endDate.isAfter(startDate)) {
            throw new IllegalArgumentException(
                    "End date must be after start date. startDate=" + startDate + ", endDate=" + endDate);
        }
    }

    public BigDecimal calculatePremium() {
        if (coverageAmount != null && coverageAmount.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal rate = switch (type) {
                case HEALTH -> HEALTH_RATE;
                case LIFE -> LIFE_RATE;
                case VEHICLE -> VEHICLE_RATE;
                case HOME -> HOME_RATE;
                case BUSINESS -> BUSINESS_RATE;
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
        validateCoverageAmount();
        validateDates();
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
        if (this.status == PolicyStatus.EXPIRED) {
            throw new IllegalStateException("Cannot cancel an expired policy.");
        }
        this.status = PolicyStatus.CANCELLED;
    }

    public void expire(String reason) {
        if (this.status == PolicyStatus.EXPIRED) {
            throw new IllegalStateException("Policy is already expired.");
        }
        if (this.status == PolicyStatus.CANCELLED) {
            throw new IllegalStateException("Cannot expire a cancelled policy.");
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
