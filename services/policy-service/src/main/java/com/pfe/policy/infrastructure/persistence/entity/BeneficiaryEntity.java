package com.pfe.policy.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "beneficiaries")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BeneficiaryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id", nullable = false)
    private PolicyEntity policy;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String relationship;

    @Column(nullable = false)
    private Double percentage;
}
