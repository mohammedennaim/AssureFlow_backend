package com.pfe.claims.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "claim_documents")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimDocumentEntity {
    @Id
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "claim_id", nullable = false)
    private ClaimEntity claim;

    @Column(nullable = false)
    private String documentName;

    private String documentUrl;

    private LocalDateTime uploadDate;
}
