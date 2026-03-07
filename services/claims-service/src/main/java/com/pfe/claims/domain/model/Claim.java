package com.pfe.claims.domain.model;

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
import java.util.UUID;

@AggregateRoot
@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Claim {
    @EqualsAndHashCode.Include
    private UUID id;
    private String claimNumber;
    private UUID policyId;
    private UUID clientId;
    private ClaimStatus status;
    private LocalDate incidentDate;
    private String description;
    private BigDecimal estimatedAmount;
    private BigDecimal approvedAmount;
    private UUID submittedBy;
    private UUID approvedBy;
    private UUID assignedTo;
    private LocalDateTime createdAt;

    @Builder.Default
    private List<ClaimDocument> documents = new ArrayList<>();

    @Builder.Default
    private List<ClaimAssessment> assessments = new ArrayList<>();

    private Payout payout;

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

    /**
     * Submits a new claim for processing.
     * A claim can only be submitted once (from a null or fresh status).
     *
     * @throws IllegalStateException if the claim has already been submitted or is
     *                               in any other active state
     */
    public void submit() {
        if (this.status != null) {
            throw new IllegalStateException(
                    "Claim has already been submitted. Current status: " + this.status);
        }
        this.status = ClaimStatus.SUBMITTED;
    }

    public void markAsUnderReview() {
        if (this.status != ClaimStatus.SUBMITTED) {
            throw new IllegalStateException("Only SUBMITTED claims can be reviewed. Current status: " + this.status);
        }
        this.status = ClaimStatus.UNDER_REVIEW;
    }

    public void approve(BigDecimal amount) {
        if (this.status != ClaimStatus.UNDER_REVIEW) {
            throw new IllegalStateException("Only claims UNDER_REVIEW can be approved. Current status: " + this.status);
        }
        this.approvedAmount = amount;
        this.status = ClaimStatus.APPROVED;
    }

    public void reject(String reason) {
        if (this.status != ClaimStatus.UNDER_REVIEW) {
            throw new IllegalStateException("Only claims UNDER_REVIEW can be rejected. Current status: " + this.status);
        }
        this.status = ClaimStatus.REJECTED;
    }

    public void requestInfo() {
        if (this.status != ClaimStatus.SUBMITTED && this.status != ClaimStatus.UNDER_REVIEW) {
            throw new IllegalStateException("Cannot request info for a claim with status: " + this.status);
        }
    }

    public void initiatePayout() {
        if (this.status != ClaimStatus.APPROVED) {
            throw new IllegalStateException(
                    "Only APPROVED claims can initiate payout. Current status: " + this.status);
        }
        this.status = ClaimStatus.PAYOUT_INITIATED;
    }

    public void markAsPaid() {
        if (this.status != ClaimStatus.APPROVED && this.status != ClaimStatus.PAYOUT_INITIATED) {
            throw new IllegalStateException(
                    "Only APPROVED or PAYOUT_INITIATED claims can be marked as paid. Current status: " + this.status);
        }
        this.status = ClaimStatus.PAID;
    }

    public void refund() {
        if (this.status != ClaimStatus.PAID) {
            throw new IllegalStateException("Only PAID claims can be refunded. Current status: " + this.status);
        }
        this.status = ClaimStatus.REFUNDED;
    }

    public void close() {
        if (this.status != ClaimStatus.PAID && this.status != ClaimStatus.REJECTED
                && this.status != ClaimStatus.REFUNDED) {
            throw new IllegalStateException(
                    "Only PAID, REJECTED or REFUNDED claims can be closed. Current status: " + this.status);
        }
        this.status = ClaimStatus.CLOSED;
    }
}
