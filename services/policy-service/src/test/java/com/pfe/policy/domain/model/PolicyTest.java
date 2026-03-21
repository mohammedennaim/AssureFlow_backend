package com.pfe.policy.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PolicyTest {

    private Policy createDraftPolicy() {
        return Policy.builder()
                .id("policy-1")
                .policyNumber("POL-ABCDEF12")
                .clientId("client-1")
                .type(PolicyType.HEALTH)
                .status(PolicyStatus.DRAFT)
                .coverageAmount(new BigDecimal("10000"))
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusYears(1))
                .createdAt(LocalDateTime.now())
                .build();
    }

    private Policy createActivePolicy() {
        Policy policy = createDraftPolicy();
        policy.submit();
        return policy;
    }

    @Nested
    @DisplayName("Premium Calculation Tests")
    class PremiumCalculationTests {

        @Test
        @DisplayName("Should calculate HEALTH premium at 5% rate")
        void shouldCalculateHealthPremium() {
            Policy policy = Policy.builder()
                    .type(PolicyType.HEALTH)
                    .coverageAmount(new BigDecimal("10000"))
                    .build();

            BigDecimal premium = policy.calculatePremium();

            assertEquals(new BigDecimal("500.00"), premium);
        }

        @Test
        @DisplayName("Should calculate LIFE premium at 3% rate")
        void shouldCalculateLifePremium() {
            Policy policy = Policy.builder()
                    .type(PolicyType.LIFE)
                    .coverageAmount(new BigDecimal("10000"))
                    .build();

            BigDecimal premium = policy.calculatePremium();

            assertEquals(new BigDecimal("300.00"), premium);
        }

        @Test
        @DisplayName("Should calculate VEHICLE premium at 7% rate")
        void shouldCalculateVehiclePremium() {
            Policy policy = Policy.builder()
                    .type(PolicyType.VEHICLE)
                    .coverageAmount(new BigDecimal("10000"))
                    .build();

            BigDecimal premium = policy.calculatePremium();

            assertEquals(new BigDecimal("700.00"), premium);
        }

        @Test
        @DisplayName("Should calculate HOME premium at 4% rate")
        void shouldCalculateHomePremium() {
            Policy policy = Policy.builder()
                    .type(PolicyType.HOME)
                    .coverageAmount(new BigDecimal("10000"))
                    .build();

            BigDecimal premium = policy.calculatePremium();

            assertEquals(new BigDecimal("400.00"), premium);
        }

        @Test
        @DisplayName("Should calculate BUSINESS premium at 6% rate")
        void shouldCalculateBusinessPremium() {
            Policy policy = Policy.builder()
                    .type(PolicyType.BUSINESS)
                    .coverageAmount(new BigDecimal("10000"))
                    .build();

            BigDecimal premium = policy.calculatePremium();

            assertEquals(new BigDecimal("600.00"), premium);
        }

        @Test
        @DisplayName("Should return null premium for zero coverage")
        void shouldNotCalculatePremiumForZeroCoverage() {
            Policy policy = Policy.builder()
                    .type(PolicyType.HEALTH)
                    .coverageAmount(BigDecimal.ZERO)
                    .build();

            BigDecimal premium = policy.calculatePremium();

            assertNull(premium);
        }

        @Test
        @DisplayName("Should return null premium for null coverage")
        void shouldNotCalculatePremiumForNullCoverage() {
            Policy policy = Policy.builder()
                    .type(PolicyType.HEALTH)
                    .coverageAmount(null)
                    .build();

            BigDecimal premium = policy.calculatePremium();

            assertNull(premium);
        }
    }

    @Nested
    @DisplayName("State Transition Tests")
    class StateTransitionTests {

        @Test
        @DisplayName("Should submit DRAFT policy → ACTIVE")
        void shouldSubmitDraftPolicy() {
            Policy policy = createDraftPolicy();

            policy.submit();

            assertEquals(PolicyStatus.ACTIVE, policy.getStatus());
        }

        @Test
        @DisplayName("Should throw when submitting non-DRAFT policy")
        void shouldNotSubmitNonDraftPolicy() {
            Policy policy = createActivePolicy();

            assertThrows(IllegalStateException.class, policy::submit);
        }

        @Test
        @DisplayName("Should cancel ACTIVE policy → CANCELLED")
        void shouldCancelActivePolicy() {
            Policy policy = createActivePolicy();

            policy.cancel("No longer needed");

            assertEquals(PolicyStatus.CANCELLED, policy.getStatus());
        }

        @Test
        @DisplayName("Should throw when cancelling already CANCELLED policy")
        void shouldNotCancelAlreadyCancelledPolicy() {
            Policy policy = createActivePolicy();
            policy.cancel("first cancel");

            assertThrows(IllegalStateException.class, () -> policy.cancel("second cancel"));
        }

        @Test
        @DisplayName("Should expire ACTIVE policy → EXPIRED")
        void shouldExpireActivePolicy() {
            Policy policy = createActivePolicy();

            policy.expire("Term ended");

            assertEquals(PolicyStatus.EXPIRED, policy.getStatus());
        }

        @Test
        @DisplayName("Should throw when expiring already EXPIRED policy")
        void shouldNotExpireAlreadyExpiredPolicy() {
            Policy policy = createActivePolicy();
            policy.expire("First expiration");

            assertThrows(IllegalStateException.class, () -> policy.expire("Second expiration"));
        }

        @Test
        @DisplayName("Should throw when expiring CANCELLED policy")
        void shouldNotExpireCancelledPolicy() {
            Policy policy = createActivePolicy();
            policy.cancel("Cancelled by client");

            assertThrows(IllegalStateException.class, () -> policy.expire("Cannot expire cancelled"));
        }

        @Test
        @DisplayName("Should allow expiring DRAFT policy")
        void shouldAllowExpiringDraftPolicy() {
            Policy policy = createDraftPolicy();

            assertDoesNotThrow(() -> policy.expire("Expired before activation"));
            assertEquals(PolicyStatus.EXPIRED, policy.getStatus());
        }

        @Test
        @DisplayName("Should reject non-CANCELLED/EXPIRED policy → CANCELLED")
        void shouldRejectPolicy() {
            Policy policy = createActivePolicy();

            policy.reject("Fraudulent claim");

            assertEquals(PolicyStatus.CANCELLED, policy.getStatus());
        }

        @Test
        @DisplayName("Should throw when rejecting CANCELLED policy")
        void shouldNotRejectCancelledPolicy() {
            Policy policy = createActivePolicy();
            policy.cancel("cancelled");

            assertThrows(IllegalStateException.class, () -> policy.reject("reason"));
        }

        @Test
        @DisplayName("Should renew ACTIVE policy")
        void shouldRenewActivePolicy() {
            Policy policy = createActivePolicy();
            Policy newPolicy = createDraftPolicy();

            assertDoesNotThrow(() -> policy.renewPolicy(newPolicy));
        }

        @Test
        @DisplayName("Should renew EXPIRED policy")
        void shouldRenewExpiredPolicy() {
            Policy policy = createActivePolicy();
            policy.expire("expired");
            Policy newPolicy = createDraftPolicy();

            assertDoesNotThrow(() -> policy.renewPolicy(newPolicy));
        }

        @Test
        @DisplayName("Should throw when renewing DRAFT policy")
        void shouldNotRenewDraftPolicy() {
            Policy policy = createDraftPolicy();
            Policy newPolicy = createDraftPolicy();

            assertThrows(IllegalStateException.class, () -> policy.renewPolicy(newPolicy));
        }
    }

    @Nested
    @DisplayName("Domain Event Tests")
    class DomainEventTests {

        @Test
        @DisplayName("Should register domain events")
        void shouldRegisterDomainEvent() {
            Policy policy = createDraftPolicy();

            policy.registerEvent(new com.pfe.policy.domain.event.PolicyCreatedEvent());

            assertFalse(policy.getDomainEvents().isEmpty());
            assertEquals(1, policy.getDomainEvents().size());
        }

        @Test
        @DisplayName("Should clear domain events")
        void shouldClearDomainEvents() {
            Policy policy = createDraftPolicy();
            policy.registerEvent(new com.pfe.policy.domain.event.PolicyCreatedEvent());

            policy.clearDomainEvents();

            assertTrue(policy.getDomainEvents().isEmpty());
        }

        @Test
        @DisplayName("Domain events list should be unmodifiable")
        void domainEventsShouldBeUnmodifiable() {
            Policy policy = createDraftPolicy();

            assertThrows(UnsupportedOperationException.class,
                    () -> policy.getDomainEvents().add(new com.pfe.policy.domain.event.PolicyCreatedEvent()));
        }
    }
}
