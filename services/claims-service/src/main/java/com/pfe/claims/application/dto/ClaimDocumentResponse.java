package com.pfe.claims.application.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimDocumentResponse {
    private String id;
    private String claimId;
    private String documentName;
    private String documentUrl;
    private LocalDateTime uploadDate;
}
