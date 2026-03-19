package com.pfe.policy.application.service.impl;

import com.pfe.commons.dto.BaseResponse;
import com.pfe.commons.exceptions.BusinessException;
import com.pfe.policy.application.dto.CreatePolicyRequest;
import com.pfe.policy.application.dto.PolicyDto;
import com.pfe.policy.application.mapper.PolicyMapper;
import com.pfe.policy.domain.model.Policy;
import com.pfe.policy.domain.model.PolicyStatus;
import com.pfe.policy.domain.model.PolicyType;
import com.pfe.policy.domain.repository.PolicyRepository;
import com.pfe.policy.infrastructure.client.ClientDto;
import com.pfe.policy.infrastructure.client.ClientServiceClient;
import com.pfe.policy.domain.exception.PolicyNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PolicyServiceImplTest {

    @Mock
    private PolicyRepository policyRepository;

    @Mock
    private PolicyMapper policyMapper;

    @Mock
    private ClientServiceClient clientServiceClient;

    @InjectMocks
    private PolicyServiceImpl policyService;

    private final String POLICY_ID = "policy-uuid-123";
    private final String CLIENT_ID = UUID.randomUUID().toString();

    private Policy createTestPolicy(PolicyStatus status) {
        return Policy.builder()
                .id(POLICY_ID)
                .policyNumber("POL-ABCDEF12")
                .clientId(CLIENT_ID)
                .type(PolicyType.HEALTH)
                .status(status)
                .coverageAmount(new BigDecimal("10000"))
                .premiumAmount(new BigDecimal("500"))
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusYears(1))
                .build();
    }

    private CreatePolicyRequest createTestRequest() {
        CreatePolicyRequest req = new CreatePolicyRequest();
        req.setClientId(CLIENT_ID);
        req.setType(PolicyType.HEALTH);
        req.setCoverageAmount(new BigDecimal("10000"));
        req.setStartDate(LocalDate.now().plusDays(1));
        req.setEndDate(LocalDate.now().plusYears(1));
        return req;
    }


    @Nested
    @DisplayName("Create Policy Tests")
    class CreatePolicyTests {

        @Test
        @DisplayName("Should create policy successfully")
        void shouldCreatePolicy() {
            CreatePolicyRequest request = createTestRequest();
            Policy domain = createTestPolicy(PolicyStatus.DRAFT);
            PolicyDto dto = new PolicyDto();
            dto.setId(POLICY_ID);

            BaseResponse<ClientDto> clientResponse = new BaseResponse<>();
            clientResponse.setSuccess(true);
            clientResponse.setData(new ClientDto());

            when(clientServiceClient.getClientById(any(String.class))).thenReturn(clientResponse);
            when(policyMapper.toDomain(request)).thenReturn(domain);
            when(policyRepository.save(any(Policy.class))).thenReturn(domain);
            when(policyMapper.toDto(domain)).thenReturn(dto);

            PolicyDto result = policyService.createPolicy(request);

            assertNotNull(result);
            assertEquals(POLICY_ID, result.getId());
            verify(policyRepository).save(any(Policy.class));
        }

        @Test
        @DisplayName("Should throw when client ID is blank")
        void shouldThrowWhenClientIdBlank() {
            CreatePolicyRequest request = createTestRequest();
            request.setClientId("");

            assertThrows(BusinessException.class, () -> policyService.createPolicy(request));
            verify(policyRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw when client not found")
        void shouldThrowWhenClientNotFound() {
            CreatePolicyRequest request = createTestRequest();

            BaseResponse<ClientDto> response = new BaseResponse<>();
            response.setSuccess(false);
            response.setData(null);

            when(clientServiceClient.getClientById(any(String.class))).thenReturn(response);

            assertThrows(BusinessException.class, () -> policyService.createPolicy(request));
            verify(policyRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Get Policy Tests")
    class GetPolicyTests {

        @Test
        @DisplayName("Should return policy by ID")
        void shouldReturnPolicyById() {
            Policy policy = createTestPolicy(PolicyStatus.ACTIVE);
            PolicyDto dto = new PolicyDto();
            dto.setId(POLICY_ID);

            when(policyRepository.findById(POLICY_ID)).thenReturn(Optional.of(policy));
            when(policyMapper.toDto(policy)).thenReturn(dto);

            PolicyDto result = policyService.getPolicyById(POLICY_ID);

            assertNotNull(result);
            assertEquals(POLICY_ID, result.getId());
        }

        @Test
        @DisplayName("Should throw when policy not found")
        void shouldThrowWhenPolicyNotFound() {
            when(policyRepository.findById("unknown")).thenReturn(Optional.empty());

            assertThrows(PolicyNotFoundException.class, () -> policyService.getPolicyById("unknown"));
        }

        @Test
        @DisplayName("Should return all policies")
        void shouldReturnAllPolicies() {
            Policy policy = createTestPolicy(PolicyStatus.ACTIVE);
            PolicyDto dto = new PolicyDto();

            when(policyRepository.findAll()).thenReturn(List.of(policy));
            when(policyMapper.toDto(policy)).thenReturn(dto);

            List<PolicyDto> result = policyService.getAllPolicies();

            assertEquals(1, result.size());
        }
    }

    @Nested
    @DisplayName("State Transition Tests")
    class StateTransitionTests {

        @Test
        @DisplayName("Should submit DRAFT policy")
        void shouldSubmitPolicy() {
            Policy policy = createTestPolicy(PolicyStatus.DRAFT);

            when(policyRepository.findById(POLICY_ID)).thenReturn(Optional.of(policy));
            when(policyRepository.save(any(Policy.class))).thenReturn(policy);

            assertDoesNotThrow(() -> policyService.submitPolicy(POLICY_ID));
            assertEquals(PolicyStatus.ACTIVE, policy.getStatus());
            verify(policyRepository).save(policy);
        }

        @Test
        @DisplayName("Should cancel ACTIVE policy")
        void shouldCancelPolicy() {
            Policy policy = createTestPolicy(PolicyStatus.ACTIVE);

            when(policyRepository.findById(POLICY_ID)).thenReturn(Optional.of(policy));
            when(policyRepository.save(any(Policy.class))).thenReturn(policy);

            assertDoesNotThrow(() -> policyService.cancelPolicy(POLICY_ID, "No longer needed"));
            assertEquals(PolicyStatus.CANCELLED, policy.getStatus());
        }

        @Test
        @DisplayName("Should reject ACTIVE policy")
        void shouldRejectPolicy() {
            Policy policy = createTestPolicy(PolicyStatus.ACTIVE);

            when(policyRepository.findById(POLICY_ID)).thenReturn(Optional.of(policy));
            when(policyRepository.save(any(Policy.class))).thenReturn(policy);

            assertDoesNotThrow(() -> policyService.rejectPolicy(POLICY_ID, "Fraud"));
            assertEquals(PolicyStatus.CANCELLED, policy.getStatus());
        }

        @Test
        @DisplayName("Should expire ACTIVE policy")
        void shouldExpirePolicy() {
            Policy policy = createTestPolicy(PolicyStatus.ACTIVE);

            when(policyRepository.findById(POLICY_ID)).thenReturn(Optional.of(policy));
            when(policyRepository.save(any(Policy.class))).thenReturn(policy);

            assertDoesNotThrow(() -> policyService.expirePolicy(POLICY_ID, "Term ended"));
            assertEquals(PolicyStatus.EXPIRED, policy.getStatus());
        }

        @Test
        @DisplayName("Should throw when submitting non-DRAFT policy")
        void shouldThrowWhenSubmittingActivePollicy() {
            Policy policy = createTestPolicy(PolicyStatus.ACTIVE);

            when(policyRepository.findById(POLICY_ID)).thenReturn(Optional.of(policy));

            assertThrows(IllegalStateException.class, () -> policyService.submitPolicy(POLICY_ID));
        }
    }
}
