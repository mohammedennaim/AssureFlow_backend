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
import com.pfe.workflow.infrastructure.messaging.SAGAStepExecutorPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SAGAOrchestratorServiceImpl implements SAGAOrchestratorService {

    private final SAGATransactionRepository sagaTransactionRepository;
    private final SAGAStepExecutorPublisher stepExecutorPublisher;

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    @Cacheable(value = "sagas", key = "#sagaId")
    public SAGATransactionDto getSagaStatus(UUID sagaId) {
        SAGATransaction saga = sagaTransactionRepository.findById(sagaId)
                .orElseThrow(() -> new SAGANotFoundException(sagaId));
        return toDto(saga);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public SAGATransactionDto startSaga(String sagaType, UUID initiatedBy) {
        SAGATransaction saga = SAGATransaction.builder()
                .sagaType(sagaType)
                .initiatedBy(initiatedBy)
                .build();

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

        stepExecutorPublisher.executeNextPendingStep(savedSaga);

        return toDto(savedSaga);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
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
            log.info("Step {} completed in SAGA {}", stepId, sagaId);
        }

        sagaTransactionRepository.save(saga);
        saga.clearDomainEvents();

        if (!allStepsCompleted) {
            stepExecutorPublisher.executeNextPendingStep(saga);
        }
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    @Transactional
    @CacheEvict(value = "sagas", key = "#sagaId")
    public void reportStepFailure(UUID sagaId, UUID stepId, String errorDetails) {
        SAGATransaction saga = sagaTransactionRepository.findById(sagaId)
                .orElseThrow(() -> new SAGANotFoundException(sagaId));

        SAGAStep step = saga.getStep(stepId);
        step.markFailed(errorDetails);

        saga.fail();
        saga.compensate();

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

        sagaTransactionRepository.save(saga);
        saga.clearDomainEvents();

        stepExecutorPublisher.executeCompensation(saga);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
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
