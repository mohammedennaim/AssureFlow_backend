package com.pfe.workflow.application.service;

import com.pfe.workflow.application.dto.SAGATransactionDto;
import com.pfe.workflow.domain.exception.SAGANotFoundException;
import com.pfe.workflow.domain.model.SAGAStep;
import com.pfe.workflow.domain.model.SAGATransaction;
import com.pfe.workflow.domain.model.SAGAStatus;
import com.pfe.workflow.domain.model.StepStatus;
import com.pfe.workflow.domain.repository.SAGATransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SAGAOrchestratorServiceImplTest {

    @Mock
    private SAGATransactionRepository sagaTransactionRepository;

    @InjectMocks
    private SAGAOrchestratorServiceImpl orchestratorService;

    private SAGATransaction saga;
    private SAGAStep step1;
    private SAGAStep step2;
    private UUID sagaId;
    private UUID step1Id;
    private UUID step2Id;

    @BeforeEach
    void setUp() {
        sagaId = UUID.randomUUID();
        step1Id = UUID.randomUUID();
        step2Id = UUID.randomUUID();

        step1 = SAGAStep.builder()
                .id(step1Id)
                .serviceName("service-a")
                .action("action-1")
                .status(StepStatus.PENDING)
                .build();

        step2 = SAGAStep.builder()
                .id(step2Id)
                .serviceName("service-b")
                .action("action-2")
                .status(StepStatus.PENDING)
                .build();

        saga = SAGATransaction.builder()
                .id(sagaId)
                .sagaType("TEST_SAGA")
                .status(SAGAStatus.STARTED)
                .initiatedBy(UUID.randomUUID())
                .build();

        saga.addStep(step1);
        saga.addStep(step2);
        saga.clearDomainEvents();
        step1.execute();
        step2.execute();
    }

    @Nested
    @DisplayName("Start SAGA Tests")
    class StartSagaTests {

        @Test
        @DisplayName("Should successfully start a new SAGA")
        void shouldStartSagaSuccessfully() {
            when(sagaTransactionRepository.save(any(SAGATransaction.class))).thenReturn(saga);

            SAGATransactionDto result = orchestratorService.startSaga("TEST_SAGA", UUID.randomUUID());

            assertThat(result).isNotNull();
            assertThat(result.getSagaType()).isEqualTo("TEST_SAGA");
            verify(sagaTransactionRepository).save(any(SAGATransaction.class));
        }
    }

    @Nested
    @DisplayName("SAGA Step Success Tests")
    class SagaStepSuccessTests {

        @Test
        @DisplayName("Should mark a step as successful and keep SAGA started if steps remain")
        void shouldReportStepSuccess() {
            when(sagaTransactionRepository.findById(sagaId)).thenReturn(Optional.of(saga));

            orchestratorService.reportStepSuccess(sagaId, step1Id);

            assertThat(step1.getStatus()).isEqualTo(StepStatus.COMPLETED);
            assertThat(saga.getStatus()).isEqualTo(SAGAStatus.STARTED); // Still started since step2 is PENDING
            verify(sagaTransactionRepository).save(saga);
        }

        @Test
        @DisplayName("Should complete SAGA when all steps are completed")
        void shouldCompleteSagaWhenAllStepsComplete() {
            when(sagaTransactionRepository.findById(sagaId)).thenReturn(Optional.of(saga));
            step2.markCompleted(); // Make step2 complete before testing step1

            orchestratorService.reportStepSuccess(sagaId, step1Id);

            assertThat(step1.getStatus()).isEqualTo(StepStatus.COMPLETED);
            assertThat(saga.getStatus()).isEqualTo(SAGAStatus.COMPLETED);
            verify(sagaTransactionRepository).save(saga);
        }
    }

    @Nested
    @DisplayName("SAGA Step Failure Tests")
    class SagaStepFailureTests {

        @Test
        @DisplayName("Should report step failure and trigger compensation on SAGA")
        void shouldReportStepFailure() {
            when(sagaTransactionRepository.findById(sagaId)).thenReturn(Optional.of(saga));

            orchestratorService.reportStepFailure(sagaId, step1Id, "Database timeout");

            assertThat(step1.getStatus()).isEqualTo(StepStatus.FAILED);
            assertThat(step1.getErrorDetails()).isEqualTo("Database timeout");
            assertThat(saga.getStatus()).isEqualTo(SAGAStatus.COMPENSATING);
            verify(sagaTransactionRepository).save(saga);
        }
    }

    @Nested
    @DisplayName("Get SAGA Status")
    class GetSagaStatusTests {

        @Test
        @DisplayName("Should get SAGA status successfully")
        void shouldGetSagaStatusSuccessfully() {
            when(sagaTransactionRepository.findById(sagaId)).thenReturn(Optional.of(saga));

            SAGATransactionDto result = orchestratorService.getSagaStatus(sagaId);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(sagaId);
            verify(sagaTransactionRepository).findById(sagaId);
        }

        @Test
        @DisplayName("Should throw exception when SAGA not found")
        void shouldThrowExceptionWhenSagaNotFound() {
            when(sagaTransactionRepository.findById(sagaId)).thenReturn(Optional.empty());

            assertThrows(SAGANotFoundException.class, () -> orchestratorService.getSagaStatus(sagaId));
        }
    }
}
