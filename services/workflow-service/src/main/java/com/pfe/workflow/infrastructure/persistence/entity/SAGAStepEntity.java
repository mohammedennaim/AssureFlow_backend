package com.pfe.workflow.infrastructure.persistence.entity;

import com.pfe.workflow.domain.model.StepStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "saga_steps")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SAGAStepEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "saga_transaction_id", nullable = false)
    private SAGATransactionEntity transaction;

    @Column(nullable = false)
    private String serviceName;

    @Column(nullable = false)
    private String action;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StepStatus status;

    private String compensationAction;

    private LocalDateTime startedAt;

    private LocalDateTime completedAt;

    @Column(columnDefinition = "TEXT")
    private String errorDetails;
}
