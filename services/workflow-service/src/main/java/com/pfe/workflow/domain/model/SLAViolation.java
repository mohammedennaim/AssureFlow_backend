package com.pfe.workflow.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "sla_violations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SLAViolation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sla_definition_id", nullable = false)
    private SLADefinition slaDefinition;

    @Column(nullable = false)
    private UUID entityId;

    @Column(nullable = false, length = 50)
    private String entityType;

    @Column(nullable = false)
    private LocalDateTime deadline;

    @Column(nullable = false)
    private LocalDateTime violatedAt;

    @Column(nullable = false)
    private Long delayMinutes;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private SLAStatus status;

    @Column(nullable = false)
    private Boolean escalated;

    @Column
    private UUID escalationId;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) {
            status = SLAStatus.VIOLATED;
        }
        if (escalated == null) {
            escalated = false;
        }
    }
}
