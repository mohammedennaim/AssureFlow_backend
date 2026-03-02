package com.pfe.policy.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PolicyDocument {
    private String id;
    private String policyId;
    private DocumentType documentType;
    private String filePath;
    private LocalDateTime uploadedAt;
    private String uploadedBy;
}
