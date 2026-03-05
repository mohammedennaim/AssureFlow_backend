package com.pfe.policy.application.service;

import com.pfe.policy.application.dto.CreatePolicyRequest;
import com.pfe.policy.application.dto.PolicyDto;
import com.pfe.policy.application.dto.UpdatePolicyRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PolicyService {
    PolicyDto createPolicy(CreatePolicyRequest request);

    PolicyDto getPolicyById(String id);

    List<PolicyDto> getPoliciesByClientId(String clientId);

    List<PolicyDto> getAllPolicies();

    Page<PolicyDto> getAllPolicies(Pageable pageable);

    PolicyDto updatePolicy(String id, UpdatePolicyRequest request);

    void cancelPolicy(String id, String reason);

    void submitPolicy(String id);

    void approvePolicy(String id);

    void rejectPolicy(String id, String reason);

    void expirePolicy(String id, String reason);

    PolicyDto renewPolicy(String id);
}
