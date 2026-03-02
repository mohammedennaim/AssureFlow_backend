package com.pfe.workflow.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskRequest {
    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    private String assignedTo;

    private String relatedEntityId;

    private String relatedEntityType;

    @NotNull(message = "Due date is required")
    private LocalDateTime dueDate;
}
