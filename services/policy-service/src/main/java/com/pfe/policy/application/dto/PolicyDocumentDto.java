package com.pfe.policy.application.dto;

import com.pfe.policy.domain.model.DocumentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PolicyDocumentDto {
    private String id;
    private DocumentType documentType;
    private String filePath;
    private String uploadedBy;
    private LocalDateTime uploadedAt;
}
