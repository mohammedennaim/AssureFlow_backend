package com.pfe.workflow.infrastructure.scheduling;

import com.pfe.workflow.application.service.SLAService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SLAMonitoringJob {

    private final SLAService slaService;

    /**
     * Check for SLA violations every 5 minutes
     */
    @Scheduled(fixedRate = 300000) // 5 minutes in milliseconds
    public void checkSLAViolations() {
        log.info("Running scheduled SLA violation check...");
        try {
            slaService.checkSLAViolations();
            log.info("SLA violation check completed successfully");
        } catch (Exception e) {
            log.error("Error during SLA violation check", e);
        }
    }
}
