package com.pfe.policy.application.service;

import com.pfe.policy.application.dto.CreatePolicyRequest;
import com.pfe.policy.application.dto.PolicyDto;
import com.pfe.policy.application.dto.UpdatePolicyRequest;

import java.util.List;

public interface PolicyService {
    PolicyDto createPolicy(CreatePolicyRequest request);

    PolicyDto getPolicyById(String id);

    List<PolicyDto> getPoliciesByClientId(String clientId);

    List<PolicyDto> getAllPolicies();

    PolicyDto updatePolicy(String id, UpdatePolicyRequest request);

    void cancelPolicy(String id);

    PolicyDto renewPolicy(String id);
}
