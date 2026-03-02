package com.pfe.policy.infrastructure.web.controller;

import com.pfe.policy.application.dto.CreatePolicyRequest;
import com.pfe.policy.application.dto.PolicyDto;
import com.pfe.policy.application.dto.UpdatePolicyRequest;
import com.pfe.policy.application.service.PolicyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/policies")
@RequiredArgsConstructor
public class PolicyController {

    private final PolicyService policyService;

    @PostMapping
    public ResponseEntity<PolicyDto> createPolicy(@RequestBody CreatePolicyRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(policyService.createPolicy(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PolicyDto> getPolicyById(@PathVariable String id) {
        return ResponseEntity.ok(policyService.getPolicyById(id));
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<PolicyDto>> getPoliciesByClientId(@PathVariable String clientId) {
        return ResponseEntity.ok(policyService.getPoliciesByClientId(clientId));
    }

    @GetMapping
    public ResponseEntity<List<PolicyDto>> getAllPolicies() {
        return ResponseEntity.ok(policyService.getAllPolicies());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<PolicyDto> updatePolicy(@PathVariable String id, @RequestBody UpdatePolicyRequest request) {
        return ResponseEntity.ok(policyService.updatePolicy(id, request));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelPolicy(@PathVariable String id) {
        policyService.cancelPolicy(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/renew")
    public ResponseEntity<PolicyDto> renewPolicy(@PathVariable String id) {
        return ResponseEntity.ok(policyService.renewPolicy(id));
    }
}
