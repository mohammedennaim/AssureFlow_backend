package com.pfe.workflow.domain.model;

public enum StepStatus {
    PENDING,
    IN_PROGRESS,
    COMPLETED,
    FAILED,
    COMPENSATING,
    COMPENSATED
}
