package com.pfe.client.application.dto;

import com.pfe.client.domain.model.DocumentType;
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
public class DocumentResponse {
    private UUID id;
    private DocumentType documentType;
    private String fileName;
    private String filePath;
    private LocalDateTime uploadedAt;
}
