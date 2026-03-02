package com.pfe.policy.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "policy_documents")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PolicyDocumentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id", nullable = false)
    private PolicyEntity policy;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String documentType;

    @Column(nullable = false)
    private String uploadedBy;

    @Column(nullable = false, updatable = false)
    private LocalDateTime uploadedAt;

    @PrePersist
    protected void onCreate() {
        this.uploadedAt = LocalDateTime.now();
    }
}
