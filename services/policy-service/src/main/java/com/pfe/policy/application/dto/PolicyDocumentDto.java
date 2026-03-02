package com.pfe.policy.application.dto;

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
    private String fileName;
    private String documentType;
    private String uploadedBy;
    private LocalDateTime uploadedAt;
}
