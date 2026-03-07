package com.pfe.claims.application.service.impl;

import com.pfe.claims.application.dto.ClaimDto;
import com.pfe.claims.application.dto.CreateClaimRequest;
import com.pfe.claims.application.mapper.ClaimMapper;
import com.pfe.claims.domain.exception.ClaimNotFoundException;
import com.pfe.claims.domain.model.Claim;
import com.pfe.claims.domain.model.ClaimStatus;
import com.pfe.claims.domain.repository.ClaimRepository;
import com.pfe.claims.infrastructure.client.PolicyDto;
import com.pfe.claims.infrastructure.client.PolicyServiceClient;
import com.pfe.commons.exceptions.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClaimServiceImplTest {

    @Mock
    private ClaimRepository claimRepository;

    @Mock
    private ClaimMapper claimMapper;

    @Mock
    private PolicyServiceClient policyServiceClient;

    @InjectMocks
    private ClaimServiceImpl claimService;

    private final UUID CLAIM_ID = UUID.randomUUID();
    private final UUID POLICY_ID = UUID.randomUUID();
    private final UUID CLIENT_ID = UUID.randomUUID();

    private Claim createTestClaim(ClaimStatus status) {
        Claim claim = new Claim();
        claim.setId(CLAIM_ID);
        claim.setClaimNumber("CLM-12345678");
        claim.setPolicyId(POLICY_ID);
        claim.setClientId(CLIENT_ID);
        claim.setStatus(status);
        claim.setDescription("Test claim description");
        claim.setEstimatedAmount(new BigDecimal("5000"));
        claim.setIncidentDate(LocalDate.now().minusDays(1));
        return claim;
    }

    @Nested
    @DisplayName("Create Claim Tests")
    class CreateClaimTests {

        @Test
        @DisplayName("Should create claim when policy is valid")
        void shouldCreateClaimSuccessfully() {
            CreateClaimRequest request = new CreateClaimRequest();
            request.setPolicyId(POLICY_ID);
            request.setClientId(CLIENT_ID);
            request.setDescription("Accident");
            request.setEstimatedAmount(new BigDecimal("5000"));
            request.setIncidentDate(LocalDate.now().minusDays(1));

            PolicyDto policyDto = new PolicyDto();
            policyDto.setPolicyNumber("POL-ABCD1234");
            policyDto.setStatus("ACTIVE");

            Claim claim = createTestClaim(ClaimStatus.SUBMITTED);
            ClaimDto dto = new ClaimDto();
            dto.setId(CLAIM_ID);

            when(policyServiceClient.getPolicyById(POLICY_ID.toString())).thenReturn(policyDto);
            when(claimMapper.toDomain(request)).thenReturn(claim);
            when(claimRepository.save(any(Claim.class))).thenReturn(claim);
            when(claimMapper.toDto(claim)).thenReturn(dto);

            ClaimDto result = claimService.createClaim(request);

            assertNotNull(result);
            assertEquals(CLAIM_ID, result.getId());
            verify(claimRepository).save(any(Claim.class));
        }

        @Test
        @DisplayName("Should throw when policy ID is null")
        void shouldThrowWhenPolicyIdNull() {
            CreateClaimRequest request = new CreateClaimRequest();
            request.setPolicyId(null);

            assertThrows(BusinessException.class, () -> claimService.createClaim(request));
            verify(claimRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw when policy is CANCELLED")
        void shouldThrowWhenPolicyCancelled() {
            CreateClaimRequest request = new CreateClaimRequest();
            request.setPolicyId(POLICY_ID);

            PolicyDto policyDto = new PolicyDto();
            policyDto.setStatus("CANCELLED");

            when(policyServiceClient.getPolicyById(POLICY_ID.toString())).thenReturn(policyDto);

            assertThrows(BusinessException.class, () -> claimService.createClaim(request));
            verify(claimRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Get Claim Tests")
    class GetClaimTests {

        @Test
        @DisplayName("Should return claim by ID")
        void shouldReturnClaimById() {
            Claim claim = createTestClaim(ClaimStatus.SUBMITTED);
            ClaimDto dto = new ClaimDto();
            dto.setId(CLAIM_ID);

            when(claimRepository.findById(CLAIM_ID)).thenReturn(Optional.of(claim));
            when(claimMapper.toDto(claim)).thenReturn(dto);

            ClaimDto result = claimService.getClaimById(CLAIM_ID);

            assertNotNull(result);
            assertEquals(CLAIM_ID, result.getId());
        }

        @Test
        @DisplayName("Should throw when claim not found")
        void shouldThrowWhenClaimNotFound() {
            UUID unknownId = UUID.randomUUID();
            when(claimRepository.findById(unknownId)).thenReturn(Optional.empty());

            assertThrows(ClaimNotFoundException.class, () -> claimService.getClaimById(unknownId));
        }
    }

    @Nested
    @DisplayName("State Transition Tests")
    class StateTransitionTests {

        @Test
        @DisplayName("Should approve claim with amount")
        void shouldApproveClaim() {
            Claim claim = createTestClaim(ClaimStatus.UNDER_REVIEW);
            UUID approvedBy = UUID.randomUUID();

            when(claimRepository.findById(CLAIM_ID)).thenReturn(Optional.of(claim));
            when(claimRepository.save(any(Claim.class))).thenReturn(claim);

            assertDoesNotThrow(() -> claimService.approveClaim(CLAIM_ID, new BigDecimal("4000"), approvedBy));
            verify(claimRepository).save(claim);
        }

        @Test
        @DisplayName("Should reject claim with reason")
        void shouldRejectClaim() {
            Claim claim = createTestClaim(ClaimStatus.UNDER_REVIEW);

            when(claimRepository.findById(CLAIM_ID)).thenReturn(Optional.of(claim));
            when(claimRepository.save(any(Claim.class))).thenReturn(claim);

            assertDoesNotThrow(() -> claimService.rejectClaim(CLAIM_ID, "Insufficient evidence"));
            verify(claimRepository).save(claim);
        }

        @Test
        @DisplayName("Should close a PAID claim")
        void shouldCloseClaim() {
            Claim claim = createTestClaim(ClaimStatus.PAID);

            when(claimRepository.findById(CLAIM_ID)).thenReturn(Optional.of(claim));
            when(claimRepository.save(any(Claim.class))).thenReturn(claim);

            assertDoesNotThrow(() -> claimService.closeClaim(CLAIM_ID));
            verify(claimRepository).save(claim);
        }

        @Test
        @DisplayName("Should delete existing claim")
        void shouldDeleteClaim() {
            Claim claim = createTestClaim(ClaimStatus.SUBMITTED);

            when(claimRepository.findById(CLAIM_ID)).thenReturn(Optional.of(claim));

            assertDoesNotThrow(() -> claimService.deleteClaim(CLAIM_ID));
            verify(claimRepository).deleteById(CLAIM_ID);
        }

        @Test
        @DisplayName("Should throw when deleting non-existent claim")
        void shouldThrowWhenDeletingNonExistentClaim() {
            UUID unknownId = UUID.randomUUID();
            when(claimRepository.findById(unknownId)).thenReturn(Optional.empty());

            assertThrows(ClaimNotFoundException.class, () -> claimService.deleteClaim(unknownId));
        }
    }
}
