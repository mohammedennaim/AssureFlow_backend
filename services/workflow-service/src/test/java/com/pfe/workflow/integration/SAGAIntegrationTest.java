package com.pfe.workflow.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pfe.workflow.application.dto.SAGATransactionDto;
import com.pfe.workflow.domain.model.SAGAStatus;
import com.pfe.workflow.domain.model.SAGATransaction;
import com.pfe.workflow.domain.repository.SAGATransactionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureTestMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestMvc
@EmbeddedKafka(partitions = 1, topics = {"policy-events", "billing-events", "saga-commands"})
@ActiveProfiles("test")
@DirtiesContext
class SAGAIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SAGATransactionRepository sagaRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Should complete policy creation SAGA successfully")
    @Transactional
    void shouldCompletePolicyCreationSAGA() throws Exception {
        String correlationId = "TEST-POLICY-" + UUID.randomUUID().toString().substring(0, 8);
        String requestBody = """
            {
                "sagaType": "POLICY_CREATION",
                "correlationId": "%s",
                "steps": [
                    {
                        "stepName": "CREATE_POLICY",
                        "serviceName": "policy-service",
                        "commandType": "CREATE_POLICY_COMMAND",
                        "requestData": {
                            "clientId": "cccccccc-0000-0000-0000-000000000001",
                            "type": "AUTO",
                            "premium": 1500.00
                        }
                    },
                    {
                        "stepName": "GENERATE_INVOICE",
                        "serviceName": "billing-service", 
                        "commandType": "GENERATE_INVOICE_COMMAND",
                        "requestData": {
                            "amount": 1500.00
                        }
                    },
                    {
                        "stepName": "ACTIVATE_POLICY",
                        "serviceName": "policy-service",
                        "commandType": "ACTIVATE_POLICY_COMMAND",
                        "requestData": {}
                    }
                ]
            }
            """.formatted(correlationId);

        // When
        mockMvc.perform(post("/api/v1/workflows/saga/start")
                .contentType("application/json")
                .content(requestBody))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.correlationId").value(correlationId))
                .andExpect(jsonPath("$.status").value("STARTED"));

        // Then - Wait for SAGA completion (async processing)
        await().atMost(30, TimeUnit.SECONDS)
                .pollInterval(1, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    SAGATransaction saga = sagaRepository.findByCorrelationId(correlationId)
                            .orElseThrow(() -> new AssertionError("SAGA not found"));
                    
                    assertThat(saga.getStatus()).isIn(SAGAStatus.COMPLETED, SAGAStatus.FAILED);
                    assertThat(saga.getSteps()).hasSize(3);
                    
                    if (saga.getStatus() == SAGAStatus.COMPLETED) {
                        saga.getSteps().forEach(step -> 
                            assertThat(step.getStatus()).isEqualTo(com.pfe.workflow.domain.model.SAGAStepStatus.COMPLETED)
                        );
                    }
                });
    }

    @Test
    @DisplayName("Should handle SAGA compensation on failure")
    @Transactional
    void shouldHandleSAGACompensationOnFailure() throws Exception {
        // Given - SAGA that will fail at billing step
        String correlationId = "TEST-FAIL-" + UUID.randomUUID().toString().substring(0, 8);
        String requestBody = """
            {
                "sagaType": "POLICY_CREATION",
                "correlationId": "%s",
                "steps": [
                    {
                        "stepName": "CREATE_POLICY",
                        "serviceName": "policy-service",
                        "commandType": "CREATE_POLICY_COMMAND",
                        "requestData": {
                            "clientId": "cccccccc-0000-0000-0000-000000000001",
                            "type": "AUTO",
                            "premium": -1.00
                        }
                    },
                    {
                        "stepName": "GENERATE_INVOICE",
                        "serviceName": "billing-service",
                        "commandType": "GENERATE_INVOICE_COMMAND",
                        "requestData": {
                            "amount": -1.00
                        }
                    }
                ]
            }
            """.formatted(correlationId);

        // When
        mockMvc.perform(post("/api/v1/workflows/saga/start")
                .contentType("application/json")
                .content(requestBody))
                .andExpect(status().isAccepted());

        // Then - Wait for SAGA failure and compensation
        await().atMost(30, TimeUnit.SECONDS)
                .pollInterval(1, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    SAGATransaction saga = sagaRepository.findByCorrelationId(correlationId)
                            .orElseThrow(() -> new AssertionError("SAGA not found"));
                    
                    assertThat(saga.getStatus()).isIn(SAGAStatus.FAILED, SAGAStatus.COMPENSATING, SAGAStatus.COMPENSATED);
                    
                    // Check that compensation steps were executed
                    if (saga.getStatus() == SAGAStatus.COMPENSATED) {
                        long compensatedSteps = saga.getSteps().stream()
                                .filter(step -> step.getStatus() == com.pfe.workflow.domain.model.SAGAStepStatus.COMPENSATED)
                                .count();
                        assertThat(compensatedSteps).isGreaterThan(0);
                    }
                });
    }

    @Test
    @DisplayName("Should handle claim processing SAGA with SLA monitoring")
    @Transactional
    void shouldHandleClaimProcessingSAGAWithSLA() throws Exception {
        // Given
        String correlationId = "TEST-CLAIM-" + UUID.randomUUID().toString().substring(0, 8);
        String requestBody = """
            {
                "sagaType": "CLAIM_PROCESSING",
                "correlationId": "%s",
                "slaDeadlineHours": 48,
                "steps": [
                    {
                        "stepName": "VALIDATE_CLAIM",
                        "serviceName": "claims-service",
                        "commandType": "VALIDATE_CLAIM_COMMAND",
                        "requestData": {
                            "claimId": "xxxxxxxx-0000-0000-0000-000000000003",
                            "policyId": "pppppppp-0000-0000-0000-000000000003"
                        }
                    },
                    {
                        "stepName": "REVIEW_CLAIM",
                        "serviceName": "claims-service",
                        "commandType": "REVIEW_CLAIM_COMMAND",
                        "requestData": {
                            "claimId": "xxxxxxxx-0000-0000-0000-000000000003"
                        }
                    },
                    {
                        "stepName": "APPROVE_CLAIM",
                        "serviceName": "claims-service",
                        "commandType": "APPROVE_CLAIM_COMMAND",
                        "requestData": {
                            "claimId": "xxxxxxxx-0000-0000-0000-000000000003",
                            "approvedAmount": 5000.00
                        }
                    }
                ]
            }
            """.formatted(correlationId);

        // When
        mockMvc.perform(post("/api/v1/workflows/saga/start")
                .contentType("application/json")
                .content(requestBody))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.correlationId").value(correlationId));

        // Then - Verify SAGA is created and SLA monitoring is active
        await().atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    SAGATransaction saga = sagaRepository.findByCorrelationId(correlationId)
                            .orElseThrow(() -> new AssertionError("SAGA not found"));
                    
                    assertThat(saga.getStatus()).isIn(SAGAStatus.STARTED, SAGAStatus.IN_PROGRESS);
                    assertThat(saga.getSlaDeadline()).isNotNull();
                    assertThat(saga.getSteps()).hasSize(3);
                });
    }

    @Test
    @DisplayName("Should retrieve SAGA status and audit trail")
    void shouldRetrieveSAGAStatusAndAuditTrail() throws Exception {
        // Given - Existing SAGA from test data
        String correlationId = "CORR-POL-001";

        // When & Then
        mockMvc.perform(post("/api/v1/workflows/saga/{correlationId}/status", correlationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.correlationId").value(correlationId))
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.steps").isArray())
                .andExpect(jsonPath("$.auditLogs").isArray());
    }

    @Test
    @DisplayName("Should handle concurrent SAGA executions")
    @Transactional
    void shouldHandleConcurrentSAGAExecutions() throws Exception {
        // Given - Multiple SAGA requests
        int concurrentSAGAs = 5;
        String[] correlationIds = new String[concurrentSAGAs];
        
        for (int i = 0; i < concurrentSAGAs; i++) {
            correlationIds[i] = "TEST-CONCURRENT-" + i + "-" + UUID.randomUUID().toString().substring(0, 8);
        }

        // When - Start multiple SAGAs concurrently
        for (String correlationId : correlationIds) {
            String requestBody = """
                {
                    "sagaType": "POLICY_CREATION",
                    "correlationId": "%s",
                    "steps": [
                        {
                            "stepName": "CREATE_POLICY",
                            "serviceName": "policy-service",
                            "commandType": "CREATE_POLICY_COMMAND",
                            "requestData": {
                                "clientId": "cccccccc-0000-0000-0000-000000000001",
                                "type": "AUTO",
                                "premium": 1000.00
                            }
                        }
                    ]
                }
                """.formatted(correlationId);

            mockMvc.perform(post("/api/v1/workflows/saga/start")
                    .contentType("application/json")
                    .content(requestBody))
                    .andExpect(status().isAccepted());
        }

        // Then - All SAGAs should be processed
        await().atMost(60, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    for (String correlationId : correlationIds) {
                        SAGATransaction saga = sagaRepository.findByCorrelationId(correlationId)
                                .orElseThrow(() -> new AssertionError("SAGA not found: " + correlationId));
                        
                        assertThat(saga.getStatus()).isIn(SAGAStatus.COMPLETED, SAGAStatus.FAILED);
                    }
                });
    }
}