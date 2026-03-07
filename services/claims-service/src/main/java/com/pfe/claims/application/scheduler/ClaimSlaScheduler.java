package com.pfe.claims.application.scheduler;

import com.pfe.claims.domain.model.Claim;
import com.pfe.claims.domain.model.ClaimStatus;
import com.pfe.claims.domain.repository.ClaimRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class ClaimSlaScheduler {

    private static final long SLA_HOURS = 48;

    private final ClaimRepository claimRepository;

    @Scheduled(fixedRateString = "${sla.check.interval-ms:3600000}")
    @Transactional
    public void checkSlaBreaches() {
        LocalDateTime slaDeadline = LocalDateTime.now().minusHours(SLA_HOURS);
        List<ClaimStatus> openStatuses = List.of(ClaimStatus.SUBMITTED, ClaimStatus.UNDER_REVIEW);

        List<Claim> breachedClaims = claimRepository.findByStatusInAndCreatedAtBefore(
                openStatuses, slaDeadline);

        if (breachedClaims.isEmpty()) {
            log.debug("[SLA] No SLA breaches detected at {}", LocalDateTime.now());
            return;
        }

        log.warn("[SLA] {} claim(s) have breached the 48h SLA deadline!", breachedClaims.size());

        for (Claim claim : breachedClaims) {
            log.warn("[SLA] ESCALATION claimId={} claimNumber={} status={} createdAt={} | Exceeded {}h SLA",
                    claim.getId(),
                    claim.getClaimNumber(),
                    claim.getStatus(),
                    claim.getCreatedAt(),
                    SLA_HOURS);

            // TODO: publish claim.sla.breached Kafka event for workflow-service to handle
            // kafkaTemplate.send("claim-events", "claim.sla.breached",
            // claim.getId().toString());
        }
    }
}
