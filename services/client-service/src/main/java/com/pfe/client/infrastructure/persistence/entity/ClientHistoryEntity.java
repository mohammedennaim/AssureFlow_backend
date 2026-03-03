package com.pfe.client.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "client_history")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID clientId;

    @Column(nullable = false)
    private String action;

    @Column(nullable = false)
    private LocalDateTime performedAt;

    private UUID performedBy;

    @PrePersist
    protected void onCreate() {
        if (this.performedAt == null) {
            this.performedAt = LocalDateTime.now();
        }
    }
}
