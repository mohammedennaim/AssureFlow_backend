package com.pfe.workflow.infrastructure.persistence.entity;

import com.pfe.workflow.domain.model.SAGAStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "saga_transactions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SAGATransactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String sagaType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SAGAStatus status;

    private UUID initiatedBy;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "transaction", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SAGAStepEntity> steps = new ArrayList<>();
}
