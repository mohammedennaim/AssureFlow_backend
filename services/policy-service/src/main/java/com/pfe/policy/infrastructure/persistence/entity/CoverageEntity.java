package com.pfe.policy.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "coverages")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoverageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id", nullable = false)
    private PolicyEntity policy;

    @Column(nullable = false)
    private String coverageType;

    @Column(nullable = false)
    private BigDecimal amount;

    private BigDecimal deductible;
}
