package com.pfe.policy.application.service;

import com.pfe.policy.application.dto.PolicyRequest;
import com.pfe.policy.application.dto.PolicyResponse;
import com.pfe.policy.domain.model.PolicyStatus;

import java.util.List;

public interface PolicyService {
    PolicyResponse createPolicy(PolicyRequest request);

    PolicyResponse getPolicyById(String id);

    PolicyResponse getPolicyByNumber(String policyNumber);

    List<PolicyResponse> getPoliciesByClientId(String clientId);

    List<PolicyResponse> getPoliciesByStatus(PolicyStatus status);

    List<PolicyResponse> getAllPolicies();

    PolicyResponse updatePolicy(String id, PolicyRequest request);

    PolicyResponse cancelPolicy(String id);

    PolicyResponse activatePolicy(String id);

    void deletePolicy(String id);
}
