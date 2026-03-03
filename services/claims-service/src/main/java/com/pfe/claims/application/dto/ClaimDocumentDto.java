package com.pfe.claims.application.dto;

import com.pfe.claims.domain.model.DocumentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimDocumentDto {
    private UUID id;
    private DocumentType documentType;
    private String filePath;
    private UUID uploadedBy;
}
