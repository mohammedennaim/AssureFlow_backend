package com.pfe.claims.infrastructure.persistence.entity;

import com.pfe.claims.domain.model.ClaimStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "claims")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String claimNumber;

    @Column(nullable = false)
    private UUID policyId;

    @Column(nullable = false)
    private UUID clientId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClaimStatus status;

    @Column(nullable = false)
    private LocalDate incidentDate;

    @Column(columnDefinition = "TEXT")
    private String description;

    private BigDecimal estimatedAmount;

    private BigDecimal approvedAmount;

    private UUID submittedBy;

    private UUID approvedBy;

    private UUID assignedTo;

    @OneToMany(mappedBy = "claim", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ClaimDocumentEntity> documents = new ArrayList<>();

    @OneToMany(mappedBy = "claim", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ClaimAssessmentEntity> assessments = new ArrayList<>();

    @OneToOne(mappedBy = "claim", cascade = CascadeType.ALL, orphanRemoval = true)
    private ClaimPayoutEntity payout;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
