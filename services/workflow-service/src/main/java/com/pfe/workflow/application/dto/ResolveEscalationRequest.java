package com.pfe.workflow.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResolveEscalationRequest {
    
    @NotNull(message = "Resolved by user ID is required")
    private UUID resolvedBy;
    
    @NotBlank(message = "Resolution description is required")
    private String resolution;
}
