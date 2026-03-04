package com.pfe.claims.domain.service;

import com.pfe.claims.domain.exception.ClaimNotFoundException;
import com.pfe.claims.domain.model.*;
import com.pfe.claims.domain.repository.ClaimAssessmentRepository;
import com.pfe.claims.domain.repository.PayoutRepository;
import com.pfe.claims.domain.repository.ClaimRepository;

import java.math.BigDecimal;
import java.util.UUID;

public class ClaimDomainService {

        private final ClaimRepository claimRepository;
        private final ClaimAssessmentRepository assessmentRepository;
        private final PayoutRepository payoutRepository;

        public ClaimDomainService(ClaimRepository claimRepository,
                        ClaimAssessmentRepository assessmentRepository,
                        PayoutRepository payoutRepository) {
                this.claimRepository = claimRepository;
                this.assessmentRepository = assessmentRepository;
                this.payoutRepository = payoutRepository;
        }

        public Claim submitClaim(Claim claim) {
                claim.submit();
                return claimRepository.save(claim);
        }

        public Claim reviewClaim(UUID claimId) {
                Claim claim = claimRepository.findById(claimId)
                                .orElseThrow(() -> new ClaimNotFoundException(claimId));
                claim.markAsUnderReview();
                return claimRepository.save(claim);
        }

        public Claim assessAndApproveClaim(UUID claimId, UUID assessorId, BigDecimal approvedAmount, String notes) {
                Claim claim = claimRepository.findById(claimId)
                                .orElseThrow(() -> new ClaimNotFoundException(claimId));

                ClaimAssessment assessment = ClaimAssessment.builder()
                                .claimId(claimId)
                                .assessorId(assessorId)
                                .decision(AssessmentDecision.APPROVED)
                                .amount(approvedAmount)
                                .status(PrescriptionStatus.COMPLETED)
                                .notes(notes)
                                .build();
                assessmentRepository.save(assessment);

                claim.approve(approvedAmount);
                return claimRepository.save(claim);
        }

        public Claim assessAndRejectClaim(UUID claimId, UUID assessorId, String reason) {
                Claim claim = claimRepository.findById(claimId)
                                .orElseThrow(() -> new ClaimNotFoundException(claimId));

                ClaimAssessment assessment = ClaimAssessment.builder()
                                .claimId(claimId)
                                .assessorId(assessorId)
                                .decision(AssessmentDecision.REJECTED)
                                .status(PrescriptionStatus.COMPLETED)
                                .notes(reason)
                                .build();
                assessmentRepository.save(assessment);

                claim.reject(reason);
                return claimRepository.save(claim);
        }

        public Claim processClaimPayout(UUID claimId, String paymentMethod, UUID payedBy, UUID authorizedBy) {
                Claim claim = claimRepository.findById(claimId)
                                .orElseThrow(() -> new ClaimNotFoundException(claimId));

                Payout payout = Payout.builder()
                                .claimId(claimId)
                                .amount(claim.getApprovedAmount())
                                .paymentMethod(paymentMethod)
                                .status(PaymentStatus.PENDING)
                                .payedBy(payedBy)
                                .authorizedBy(authorizedBy)
                                .build();
                payoutRepository.save(payout);

                claim.markAsPaid();
                claim.setPayout(payout);
                return claimRepository.save(claim);
        }

        public Claim requestInfo(UUID claimId) {
                Claim claim = claimRepository.findById(claimId)
                                .orElseThrow(() -> new ClaimNotFoundException(claimId));
                claim.requestInfo();
                return claimRepository.save(claim);
        }

        public Claim closeClaim(UUID claimId) {
                Claim claim = claimRepository.findById(claimId)
                                .orElseThrow(() -> new ClaimNotFoundException(claimId));
                claim.close();
                return claimRepository.save(claim);
        }
}
