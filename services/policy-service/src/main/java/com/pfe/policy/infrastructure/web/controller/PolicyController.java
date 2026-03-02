package com.pfe.policy.infrastructure.web.controller;

import com.pfe.policy.application.dto.PolicyRequest;
import com.pfe.policy.application.dto.PolicyResponse;
import com.pfe.policy.application.service.PolicyService;
import com.pfe.policy.domain.model.PolicyStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/policies")
@RequiredArgsConstructor
@Tag(name = "Policies API", description = "Endpoints for managing insurance policies")
public class PolicyController {

    private final PolicyService policyService;

    @PostMapping
    @Operation(summary = "Create a new policy")
    public ResponseEntity<PolicyResponse> createPolicy(@Valid @RequestBody PolicyRequest request) {
        return new ResponseEntity<>(policyService.createPolicy(request), HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get all policies")
    public ResponseEntity<List<PolicyResponse>> getAllPolicies() {
        return ResponseEntity.ok(policyService.getAllPolicies());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a policy by ID")
    public ResponseEntity<PolicyResponse> getPolicyById(@PathVariable String id) {
        return ResponseEntity.ok(policyService.getPolicyById(id));
    }

    @GetMapping("/number/{policyNumber}")
    @Operation(summary = "Get a policy by policy number")
    public ResponseEntity<PolicyResponse> getPolicyByNumber(@PathVariable String policyNumber) {
        return ResponseEntity.ok(policyService.getPolicyByNumber(policyNumber));
    }

    @GetMapping("/client/{clientId}")
    @Operation(summary = "Get policies by client ID")
    public ResponseEntity<List<PolicyResponse>> getPoliciesByClientId(@PathVariable String clientId) {
        return ResponseEntity.ok(policyService.getPoliciesByClientId(clientId));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get policies by status")
    public ResponseEntity<List<PolicyResponse>> getPoliciesByStatus(@PathVariable PolicyStatus status) {
        return ResponseEntity.ok(policyService.getPoliciesByStatus(status));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a policy")
    public ResponseEntity<PolicyResponse> updatePolicy(
            @PathVariable String id,
            @Valid @RequestBody PolicyRequest request) {
        return ResponseEntity.ok(policyService.updatePolicy(id, request));
    }

    @PatchMapping("/{id}/cancel")
    @Operation(summary = "Cancel a policy")
    public ResponseEntity<PolicyResponse> cancelPolicy(@PathVariable String id) {
        return ResponseEntity.ok(policyService.cancelPolicy(id));
    }

    @PatchMapping("/{id}/activate")
    @Operation(summary = "Activate a policy")
    public ResponseEntity<PolicyResponse> activatePolicy(@PathVariable String id) {
        return ResponseEntity.ok(policyService.activatePolicy(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a policy")
    public ResponseEntity<Void> deletePolicy(@PathVariable String id) {
        policyService.deletePolicy(id);
        return ResponseEntity.noContent().build();
    }
}
