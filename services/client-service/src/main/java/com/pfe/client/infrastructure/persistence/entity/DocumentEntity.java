package com.pfe.client.infrastructure.persistence.entity;

import com.pfe.client.domain.model.DocumentType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "documents")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String clientId;

    @Enumerated(EnumType.STRING)
    private DocumentType documentType;

    private String fileName;

    private String filePath;

    private LocalDateTime uploadedAt;
}
