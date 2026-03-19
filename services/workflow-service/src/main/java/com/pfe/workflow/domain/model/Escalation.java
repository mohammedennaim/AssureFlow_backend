package com.pfe.workflow.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "escalations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Escalation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID entityId;

    @Column(nullable = false, length = 50)
    private String entityType;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private EscalationLevel level;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private EscalationStatus status;

    @Column(nullable = false, length = 100)
    private String reason;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column
    private UUID assignedTo;

    @Column(length = 100)
    private String assignedToName;

    @Column
    private UUID slaViolationId;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime resolvedAt;

    @Column
    private UUID resolvedBy;

    @Column(columnDefinition = "TEXT")
    private String resolution;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) {
            status = EscalationStatus.OPEN;
        }
        if (level == null) {
            level = EscalationLevel.LEVEL_1;
        }
    }
}
