package com.pfe.client.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Document {
    private UUID id;
    private UUID clientId;
    private DocumentType documentType;
    private String fileName;
    private String filePath;
    private LocalDateTime uploadedAt;
}
