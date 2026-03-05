package com.pfe.workflow.application.service;

import com.pfe.workflow.application.dto.SAGAStepDto;
import com.pfe.workflow.application.dto.SAGATransactionDto;
import com.pfe.workflow.domain.event.SAGACompletedEvent;
import com.pfe.workflow.domain.event.SAGAFailedEvent;
import com.pfe.workflow.domain.event.SAGAStartedEvent;
import com.pfe.workflow.domain.exception.SAGANotFoundException;
import com.pfe.workflow.domain.model.SAGAStep;
import com.pfe.workflow.domain.model.SAGATransaction;
import com.pfe.workflow.domain.model.StepStatus;
import com.pfe.workflow.domain.repository.SAGATransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SAGAOrchestratorServiceImpl implements SAGAOrchestratorService {

    private final SAGATransactionRepository sagaTransactionRepository;

    @Override
    @Cacheable(value = "sagas", key = "#sagaId")
    public SAGATransactionDto getSagaStatus(UUID sagaId) {
        SAGATransaction saga = sagaTransactionRepository.findById(sagaId)
                .orElseThrow(() -> new SAGANotFoundException(sagaId));
        return toDto(saga);
    }

    @Override
    @Transactional
    public SAGATransactionDto startSaga(String sagaType, UUID initiatedBy) {
        SAGATransaction saga = SAGATransaction.builder()
                .sagaType(sagaType)
                .initiatedBy(initiatedBy)
                .build();

        // This is a placeholder for generating the specific steps based on sagaType.
        // In a real scenario, you would have a SAGA factory or registry to attach the
        // right steps.

        saga.start();

        SAGAStartedEvent event = SAGAStartedEvent.builder()
                .sagaId(saga.getId())
                .sagaType(saga.getSagaType())
                .initiatedBy(saga.getInitiatedBy())
                .source("workflow-service")
                .build();
        saga.registerEvent(event);

        SAGATransaction savedSaga = sagaTransactionRepository.save(saga);
        log.info("Started SAGA: {} of type {}", savedSaga.getId(), sagaType);

        saga.clearDomainEvents();
        return toDto(savedSaga);
    }

    @Override
    @Transactional
    @CacheEvict(value = "sagas", key = "#sagaId")
    public void reportStepSuccess(UUID sagaId, UUID stepId) {
        SAGATransaction saga = sagaTransactionRepository.findById(sagaId)
                .orElseThrow(() -> new SAGANotFoundException(sagaId));

        SAGAStep step = saga.getStep(stepId);
        step.markCompleted();

        boolean allStepsCompleted = saga.getSteps().stream()
                .allMatch(s -> s.getStatus() == StepStatus.COMPLETED);

        if (allStepsCompleted) {
            saga.complete();
            SAGACompletedEvent event = SAGACompletedEvent.builder()
                    .sagaId(saga.getId())
                    .sagaType(saga.getSagaType())
                    .source("workflow-service")
                    .build();
            saga.registerEvent(event);
            log.info("SAGA completed successfully: {}", sagaId);
        } else {
            // Logic to trigger the NEXT step could go here, or be handled by event
            // listeners
            log.info("Step {} completed in SAGA {}", stepId, sagaId);
        }

        sagaTransactionRepository.save(saga);
        saga.clearDomainEvents();
    }

    @Override
    @Transactional
    @CacheEvict(value = "sagas", key = "#sagaId")
    public void reportStepFailure(UUID sagaId, UUID stepId, String errorDetails) {
        SAGATransaction saga = sagaTransactionRepository.findById(sagaId)
                .orElseThrow(() -> new SAGANotFoundException(sagaId));

        SAGAStep step = saga.getStep(stepId);
        step.markFailed(errorDetails);

        saga.fail(); // Marks the whole SAGA as failed
        saga.compensate(); // Moves the SAGA to COMPENSATING state

        SAGAFailedEvent event = SAGAFailedEvent.builder()
                .sagaId(saga.getId())
                .sagaType(saga.getSagaType())
                .failingStepId(stepId)
                .failingServiceName(step.getServiceName())
                .errorDetails(errorDetails)
                .source("workflow-service")
                .build();
        saga.registerEvent(event);

        log.warn("SAGA pending compensation: {} due to failure in step {}", sagaId, stepId);

        // Trigger compensation logic here (e.g., publish compensation commands via
        // Kafka)

        sagaTransactionRepository.save(saga);
        saga.clearDomainEvents();
    }

    @Override
    @Transactional
    @CacheEvict(value = "sagas", key = "#sagaId")
    public void reportCompensationSuccess(UUID sagaId, UUID stepId) {
        SAGATransaction saga = sagaTransactionRepository.findById(sagaId)
                .orElseThrow(() -> new SAGANotFoundException(sagaId));

        SAGAStep step = saga.getStep(stepId);
        step.markCompensated();

        boolean allCompensated = saga.getSteps().stream()
                .filter(s -> s.getCompensationAction() != null && !s.getCompensationAction().trim().isEmpty())
                .allMatch(s -> s.getStatus() == StepStatus.COMPENSATED || s.getStatus() == StepStatus.PENDING);

        if (allCompensated) {
            saga.markCompensated();
            log.info("SAGA fully compensated: {}", sagaId);
        }

        sagaTransactionRepository.save(saga);
    }

    // Simple manual mapper for now to avoid MapStruct setup complexity until we
    // reach the Infrastructure layer
    private SAGATransactionDto toDto(SAGATransaction saga) {
        if (saga == null)
            return null;

        return SAGATransactionDto.builder()
                .id(saga.getId())
                .sagaType(saga.getSagaType())
                .status(saga.getStatus())
                .initiatedBy(saga.getInitiatedBy())
                .createdAt(saga.getCreatedAt())
                .updatedAt(saga.getUpdatedAt())
                .steps(saga.getSteps().stream().map(this::toDto).collect(Collectors.toList()))
                .build();
    }

    private SAGAStepDto toDto(SAGAStep step) {
        if (step == null)
            return null;

        return SAGAStepDto.builder()
                .id(step.getId())
                .serviceName(step.getServiceName())
                .action(step.getAction())
                .status(step.getStatus())
                .compensationAction(step.getCompensationAction())
                .startedAt(step.getStartedAt())
                .completedAt(step.getCompletedAt())
                .errorDetails(step.getErrorDetails())
                .build();
    }
}
