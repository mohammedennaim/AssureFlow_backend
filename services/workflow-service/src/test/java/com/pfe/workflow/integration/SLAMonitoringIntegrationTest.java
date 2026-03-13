package com.pfe.workflow.integration;

import com.pfe.workflow.application.service.SLAMonitoringService;
import com.pfe.workflow.domain.model.SLAMonitoring;
import com.pfe.workflow.domain.model.SLAStatus;
import com.pfe.workflow.domain.repository.SLAMonitoringRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = {"sla-events", "escalation-events"})
@ActiveProfiles("test")
@DirtiesContext
class SLAMonitoringIntegrationTest {

    @Autowired
    private SLAMonitoringService slaMonitoringService;

    @Autowired
    private SLAMonitoringRepository slaRepository;

    @Test
    @DisplayName("Should create SLA monitoring for new claim")
    @Transactional
    void shouldCreateSLAMonitoringForNewClaim() {
        // Given
        String claimId = UUID.randomUUID().toString();
        LocalDateTime deadline = LocalDateTime.now().plusHours(48);

        // When
        slaMonitoringService.createSLAMonitoring(
                "CLAIM", 
                claimId, 
                "REVIEW_SLA", 
                deadline
        );

        // Then
        SLAMonitoring sla = slaRepository.findByResourceTypeAndResourceId("CLAIM", claimId)
                .orElseThrow(() -> new AssertionError("SLA monitoring not created"));

        assertThat(sla.getResourceType()).isEqualTo("CLAIM");
        assertThat(sla.getResourceId()).isEqualTo(claimId);
        assertThat(sla.getSlaType()).isEqualTo("REVIEW_SLA");
        assertThat(sla.getDeadline()).isEqualTo(deadline);
        assertThat(sla.getStatus()).isEqualTo(SLAStatus.ACTIVE);
        assertThat(sla.isEscalated()).isFalse();
    }

    @Test
    @DisplayName("Should escalate SLA when deadline is approaching")
    @Transactional
    void shouldEscalateSLAWhenDeadlineApproaching() {
        // Given - SLA with deadline in 1 hour (should trigger warning)
        String claimId = UUID.randomUUID().toString();
        LocalDateTime deadline = LocalDateTime.now().plusHours(1);
        
        slaMonitoringService.createSLAMonitoring(
                "CLAIM", 
                claimId, 
                "REVIEW_SLA", 
                deadline
        );

        // When - Run SLA monitoring check
        slaMonitoringService.checkSLADeadlines();

        // Then - SLA should be escalated
        await().atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    SLAMonitoring sla = slaRepository.findByResourceTypeAndResourceId("CLAIM", claimId)
                            .orElseThrow(() -> new AssertionError("SLA not found"));

                    assertThat(sla.getStatus()).isEqualTo(SLAStatus.WARNING);
                    assertThat(sla.isEscalated()).isTrue();
                    assertThat(sla.getEscalationLevel()).isEqualTo(1);
                    assertThat(sla.getEscalatedTo()).isNotNull();
                });
    }

    @Test
    @DisplayName("Should mark SLA as violated when deadline passed")
    @Transactional
    void shouldMarkSLAAsViolatedWhenDeadlinePassed() {
        // Given - SLA with deadline in the past
        String claimId = UUID.randomUUID().toString();
        LocalDateTime deadline = LocalDateTime.now().minusHours(2);
        
        slaMonitoringService.createSLAMonitoring(
                "CLAIM", 
                claimId, 
                "REVIEW_SLA", 
                deadline
        );

        // When - Run SLA monitoring check
        slaMonitoringService.checkSLADeadlines();

        // Then - SLA should be marked as violated
        await().atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    SLAMonitoring sla = slaRepository.findByResourceTypeAndResourceId("CLAIM", claimId)
                            .orElseThrow(() -> new AssertionError("SLA not found"));

                    assertThat(sla.getStatus()).isEqualTo(SLAStatus.VIOLATED);
                    assertThat(sla.isEscalated()).isTrue();
                    assertThat(sla.getEscalationLevel()).isGreaterThan(1);
                });
    }

    @Test
    @DisplayName("Should complete SLA when resource is processed")
    @Transactional
    void shouldCompleteSLAWhenResourceProcessed() {
        // Given
        String claimId = UUID.randomUUID().toString();
        LocalDateTime deadline = LocalDateTime.now().plusHours(24);
        
        slaMonitoringService.createSLAMonitoring(
                "CLAIM", 
                claimId, 
                "REVIEW_SLA", 
                deadline
        );

        // When - Mark claim as completed
        slaMonitoringService.completeSLA("CLAIM", claimId);

        // Then - SLA should be completed
        SLAMonitoring sla = slaRepository.findByResourceTypeAndResourceId("CLAIM", claimId)
                .orElseThrow(() -> new AssertionError("SLA not found"));

        assertThat(sla.getStatus()).isEqualTo(SLAStatus.COMPLETED);
        assertThat(sla.getCompletedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should handle multiple escalation levels")
    @Transactional
    void shouldHandleMultipleEscalationLevels() {
        // Given - SLA that's already been escalated once
        String claimId = UUID.randomUUID().toString();
        LocalDateTime deadline = LocalDateTime.now().minusHours(4); // Well past deadline
        
        SLAMonitoring sla = SLAMonitoring.builder()
                .resourceType("CLAIM")
                .resourceId(claimId)
                .slaType("REVIEW_SLA")
                .deadline(deadline)
                .status(SLAStatus.WARNING)
                .escalated(true)
                .escalationLevel(1)
                .escalatedTo("aaaaaaaa-0000-0000-0000-000000000007") // OPS team
                .build();
        
        slaRepository.save(sla);

        // When - Run SLA check again (should escalate further)
        slaMonitoringService.checkSLADeadlines();

        // Then - Should escalate to higher level
        await().atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    SLAMonitoring updatedSla = slaRepository.findByResourceTypeAndResourceId("CLAIM", claimId)
                            .orElseThrow(() -> new AssertionError("SLA not found"));

                    assertThat(updatedSla.getStatus()).isEqualTo(SLAStatus.VIOLATED);
                    assertThat(updatedSla.getEscalationLevel()).isEqualTo(2);
                    assertThat(updatedSla.getEscalatedTo()).isEqualTo("aaaaaaaa-0000-0000-0000-000000000001"); // Admin
                });
    }

    @Test
    @DisplayName("Should generate SLA reports")
    void shouldGenerateSLAReports() {
        // Given - Multiple SLAs with different statuses
        String[] claimIds = {
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString()
        };

        // Create SLAs with different statuses
        slaMonitoringService.createSLAMonitoring("CLAIM", claimIds[0], "REVIEW_SLA", LocalDateTime.now().plusHours(24));
        slaMonitoringService.createSLAMonitoring("CLAIM", claimIds[1], "REVIEW_SLA", LocalDateTime.now().minusHours(2));
        slaMonitoringService.createSLAMonitoring("CLAIM", claimIds[2], "REVIEW_SLA", LocalDateTime.now().plusHours(1));

        // Process one SLA
        slaMonitoringService.completeSLA("CLAIM", claimIds[0]);

        // Run SLA checks
        slaMonitoringService.checkSLADeadlines();

        // When - Generate SLA report
        var report = slaMonitoringService.generateSLAReport(
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1)
        );

        // Then - Report should contain all SLAs
        assertThat(report).isNotNull();
        assertThat(report.getTotalSLAs()).isEqualTo(3);
        assertThat(report.getCompletedSLAs()).isEqualTo(1);
        assertThat(report.getViolatedSLAs()).isGreaterThanOrEqualTo(1);
        assertThat(report.getActiveSLAs()).isGreaterThanOrEqualTo(1);
    }
}