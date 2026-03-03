package com.pfe.claims.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimDocument {
    private UUID id;
    private UUID claimId;
    private DocumentType documentType;
    private String filePath;
    private UUID uploadedBy;
}
