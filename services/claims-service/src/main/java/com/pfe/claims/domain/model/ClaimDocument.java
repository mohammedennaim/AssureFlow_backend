package com.pfe.claims.domain.model;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimDocument {
    private String id;
    private String claimId;
    private String documentName;
    private String documentUrl;
    private LocalDateTime uploadDate;
}
