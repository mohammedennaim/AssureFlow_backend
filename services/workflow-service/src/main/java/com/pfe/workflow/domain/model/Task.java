package com.pfe.workflow.domain.model;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Task {
    private String id;
    private String title;
    private String description;
    private String assignedTo;
    private String relatedEntityId;
    private String relatedEntityType;
    private TaskStatus status;
    private LocalDateTime dueDate;
    private LocalDateTime completedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public void start() {
        this.status = TaskStatus.IN_PROGRESS;
    }

    public void complete() {
        this.status = TaskStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
    }

    public void cancel() {
        this.status = TaskStatus.CANCELLED;
    }
}
