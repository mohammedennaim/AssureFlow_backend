package com.pfe.workflow.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SAGATransaction {
    private UUID id;
    private String sagaType;
    private SAGAStatus status;
    private UUID initiatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder.Default
    private List<SAGAStep> steps = new ArrayList<>();

    @Builder.Default
    private transient List<Object> domainEvents = new ArrayList<>();

    public void registerEvent(Object event) {
        if (this.domainEvents == null) {
            this.domainEvents = new ArrayList<>();
        }
        this.domainEvents.add(event);
    }

    public List<Object> getDomainEvents() {
        if (this.domainEvents == null) {
            return new ArrayList<>();
        }
        return java.util.Collections.unmodifiableList(this.domainEvents);
    }

    public void clearDomainEvents() {
        if (this.domainEvents != null) {
            this.domainEvents.clear();
        }
    }

    public void addStep(SAGAStep step) {
        if (this.steps == null) {
            this.steps = new ArrayList<>();
        }
        this.steps.add(step);
    }

    public void start() {
        if (this.status != null && this.status != SAGAStatus.STARTED) {
            throw new IllegalStateException("SAGA cannot enter STARTED state from: " + this.status);
        }
        this.status = SAGAStatus.STARTED;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void markInProgress() {
        if (this.status != SAGAStatus.STARTED && this.status != SAGAStatus.IN_PROGRESS) {
            throw new IllegalStateException("SAGA cannot enter IN_PROGRESS state from: " + this.status);
        }
        this.status = SAGAStatus.IN_PROGRESS;
        this.updatedAt = LocalDateTime.now();
    }

    public void complete() {
        if (this.status != SAGAStatus.IN_PROGRESS && this.status != SAGAStatus.STARTED) {
            throw new IllegalStateException(
                    "SAGA must be IN_PROGRESS or STARTED to be completed. Current: " + this.status);
        }
        boolean allCompleted = steps.stream().allMatch(s -> s.getStatus() == StepStatus.COMPLETED);
        if (!allCompleted) {
            throw new IllegalStateException("Cannot complete SAGA: not all steps are completed.");
        }
        this.status = SAGAStatus.COMPLETED;
        this.updatedAt = LocalDateTime.now();
    }

    public void fail() {
        if (this.status == SAGAStatus.COMPLETED || this.status == SAGAStatus.COMPENSATED) {
            throw new IllegalStateException("Cannot fail a SAGA in terminal state: " + this.status);
        }
        this.status = SAGAStatus.FAILED;
        this.updatedAt = LocalDateTime.now();
    }

    public void compensate() {
        if (this.status != SAGAStatus.FAILED && this.status != SAGAStatus.COMPENSATING) {
            throw new IllegalStateException("SAGA must be FAILED to start compensation. Current: " + this.status);
        }
        this.status = SAGAStatus.COMPENSATING;
        this.updatedAt = LocalDateTime.now();
    }

    public void markCompensated() {
        if (this.status != SAGAStatus.COMPENSATING) {
            throw new IllegalStateException(
                    "SAGA must be COMPENSATING to be marked COMPENSATED. Current: " + this.status);
        }
        boolean allCompensated = steps.stream()
                .filter(s -> s.getCompensationAction() != null && !s.getCompensationAction().trim().isEmpty())
                .allMatch(s -> s.getStatus() == StepStatus.COMPENSATED || s.getStatus() == StepStatus.PENDING);

        if (!allCompensated) {
            throw new IllegalStateException("Not all compensatable steps have been compensated.");
        }

        this.status = SAGAStatus.COMPENSATED;
        this.updatedAt = LocalDateTime.now();
    }

    public SAGAStep getStep(UUID stepId) {
        return steps.stream()
                .filter(s -> s.getId().equals(stepId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Step not found with ID: " + stepId));
    }
}
