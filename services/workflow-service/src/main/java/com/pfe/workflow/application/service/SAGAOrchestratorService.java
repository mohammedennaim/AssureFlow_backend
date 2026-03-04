package com.pfe.workflow.application.service;

import com.pfe.workflow.application.dto.SAGATransactionDto;

import java.util.UUID;

public interface SAGAOrchestratorService {

    /**
     * Retrieves the current state of a SAGA transaction.
     */
    SAGATransactionDto getSagaStatus(UUID sagaId);

    /**
     * Starts a new SAGA transaction (e.g., Claim Processing, Policy Creation).
     */
    SAGATransactionDto startSaga(String sagaType, UUID initiatedBy);

    /**
     * Reports that a specific step in the SAGA has been completed successfully.
     */
    void reportStepSuccess(UUID sagaId, UUID stepId);

    /**
     * Reports that a specific step in the SAGA has failed, which should trigger
     * compensation.
     */
    void reportStepFailure(UUID sagaId, UUID stepId, String errorDetails);

    /**
     * Reports that a compensation step has been completed.
     */
    void reportCompensationSuccess(UUID sagaId, UUID stepId);
}
