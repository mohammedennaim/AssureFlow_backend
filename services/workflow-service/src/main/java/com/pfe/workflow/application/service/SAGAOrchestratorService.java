package com.pfe.workflow.application.service;

import com.pfe.workflow.application.dto.SAGATransactionDto;

import java.util.UUID;

public interface SAGAOrchestratorService {
    SAGATransactionDto getSagaStatus(UUID sagaId);
    SAGATransactionDto startSaga(String sagaType, UUID initiatedBy);
    void reportStepSuccess(UUID sagaId, UUID stepId);
    void reportStepFailure(UUID sagaId, UUID stepId, String errorDetails);
    void reportCompensationSuccess(UUID sagaId, UUID stepId);
}
