package com.pfe.client.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "client_history")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String clientId;

    @Column(nullable = false)
    private String action;

    @Column(nullable = false)
    private LocalDateTime performedAt;

    private String performedBy;

    @PrePersist
    protected void onCreate() {
        if (this.performedAt == null) {
            this.performedAt = LocalDateTime.now();
        }
    }
}
