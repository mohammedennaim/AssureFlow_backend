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
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id", nullable = false)
    private PolicyEntity policy;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private BigDecimal coverageLimit;

    private String description;
}
