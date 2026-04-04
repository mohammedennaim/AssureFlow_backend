package com.pfe.workflow.application.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateSLADefinitionRequest {
    
    @NotBlank(message = "Name is required")
    private String name;
    
    @NotBlank(message = "Entity type is required")
    private String entityType;
    
    private String description;
    
    @NotNull(message = "Duration in hours is required")
    @Min(value = 1, message = "Duration must be at least 1 hour")
    private Integer durationHours;
    
    @Builder.Default
    private Boolean autoEscalate = true;
    @Builder.Default
    private Boolean active = true;
}
