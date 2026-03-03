package com.pfe.claims.infrastructure.persistence.entity;

import com.pfe.claims.domain.model.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "claim_payouts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimPayoutEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "claim_id", nullable = false, unique = true)
    private ClaimEntity claim;

    @Column(nullable = false)
    private BigDecimal amount;

    private String paymentMethod;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    private UUID payedBy;

    private UUID authorizedBy;
}
