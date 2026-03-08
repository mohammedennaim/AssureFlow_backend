package com.pfe.workflow.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SAGAStep {
    private UUID id;
    private String serviceName;
    private String action;
    private StepStatus status;
    private String compensationAction;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private String errorDetails;

    public void execute() {
        if (this.status != StepStatus.PENDING && this.status != StepStatus.FAILED) {
            throw new IllegalStateException("Cannot execute step in status: " + this.status);
        }
        this.status = StepStatus.IN_PROGRESS;
        this.startedAt = LocalDateTime.now();
    }

    public void markCompleted() {
        if (this.status != StepStatus.IN_PROGRESS) {
            throw new IllegalStateException("Cannot complete a step that is not IN_PROGRESS. Status: " + this.status);
        }
        this.status = StepStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
    }

    public void markFailed(String errorDetails) {
        if (this.status != StepStatus.IN_PROGRESS) {
            throw new IllegalStateException("Cannot fail a step that is not IN_PROGRESS. Status: " + this.status);
        }
        this.status = StepStatus.FAILED;
        this.errorDetails = errorDetails;
        this.completedAt = LocalDateTime.now();
    }

    public void compensate() {
        if (this.status != StepStatus.COMPLETED && this.status != StepStatus.FAILED) {
            throw new IllegalStateException("Cannot compensate step in status: " + this.status);
        }
        if (this.compensationAction == null || this.compensationAction.trim().isEmpty()) {
            throw new IllegalStateException("No compensation action defined for this step");
        }
        this.status = StepStatus.COMPENSATING;
    }

    public void markCompensated() {
        if (this.status != StepStatus.COMPENSATING) {
            throw new IllegalStateException("Cannot mark as compensated a step not in COMPENSATING state.");
        }
        this.status = StepStatus.COMPENSATED;
    }
}
