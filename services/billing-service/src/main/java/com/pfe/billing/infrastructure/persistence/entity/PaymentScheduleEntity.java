package com.pfe.billing.infrastructure.persistence.entity;

import com.pfe.billing.domain.model.PaymentFrequency;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "payment_schedules")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentScheduleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID policyId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentFrequency frequency;

    @Column(nullable = false)
    private LocalDate nextDueDate;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;
}
