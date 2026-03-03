package com.pfe.claims.infrastructure.persistence.entity;

import com.pfe.claims.domain.model.AssessmentDecision;
import com.pfe.claims.domain.model.PrescriptionStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "claim_assessments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimAssessmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "claim_id", nullable = false)
    private ClaimEntity claim;

    private UUID assessorId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssessmentDecision decision;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private PrescriptionStatus status;

    @Column(columnDefinition = "TEXT")
    private String notes;
}
