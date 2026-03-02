package com.pfe.policy.infrastructure.web.controller;

import com.pfe.policy.application.dto.CreatePolicyRequest;
import com.pfe.policy.application.dto.PolicyDto;
import com.pfe.policy.application.dto.UpdatePolicyRequest;
import com.pfe.policy.application.service.PolicyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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

    @Operation(summary = "Create a new policy", description = "Creates a new insurance policy with its coverages and beneficiaries")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Policy created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input provided")
    })
    @PostMapping
    public ResponseEntity<PolicyDto> createPolicy(@RequestBody CreatePolicyRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(policyService.createPolicy(request));
    }

    @Operation(summary = "Get a policy by ID", description = "Retrieves a specific policy using its unique ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Policy found"),
            @ApiResponse(responseCode = "404", description = "Policy not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<PolicyDto> getPolicyById(
            @Parameter(description = "The unique ID of the policy") @PathVariable String id) {
        return ResponseEntity.ok(policyService.getPolicyById(id));
    }

    @Operation(summary = "Get policies by Client ID", description = "Retrieves all policies associated with a specific client")
    @ApiResponse(responseCode = "200", description = "List of policies retrieved successfully")
    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<PolicyDto>> getPoliciesByClientId(
            @Parameter(description = "The unique ID of the client") @PathVariable String clientId) {
        return ResponseEntity.ok(policyService.getPoliciesByClientId(clientId));
    }

    @Operation(summary = "Get all policies", description = "Retrieves all policies in the system")
    @ApiResponse(responseCode = "200", description = "List of all policies retrieved successfully")
    @GetMapping
    public ResponseEntity<List<PolicyDto>> getAllPolicies() {
        return ResponseEntity.ok(policyService.getAllPolicies());
    }

    @Operation(summary = "Update an existing policy", description = "Updates specific fields of an existing policy (e.g., end date)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Policy updated successfully"),
            @ApiResponse(responseCode = "404", description = "Policy not found")
    })
    @PatchMapping("/{id}")
    public ResponseEntity<PolicyDto> updatePolicy(
            @Parameter(description = "The unique ID of the policy to update") @PathVariable String id,
            @RequestBody UpdatePolicyRequest request) {
        return ResponseEntity.ok(policyService.updatePolicy(id, request));
    }

    @Operation(summary = "Cancel a policy", description = "Changes the status of a policy to CANCELLED")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Policy cancelled successfully"),
            @ApiResponse(responseCode = "404", description = "Policy not found")
    })
    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelPolicy(
            @Parameter(description = "The unique ID of the policy to cancel") @PathVariable String id) {
        policyService.cancelPolicy(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Renew a policy", description = "Creates a new draft policy based on an existing one with new dates")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Policy renewed successfully"),
            @ApiResponse(responseCode = "404", description = "Policy not found")
    })
    @PostMapping("/{id}/renew")
    public ResponseEntity<PolicyDto> renewPolicy(
            @Parameter(description = "The unique ID of the policy to renew") @PathVariable String id) {
        return ResponseEntity.ok(policyService.renewPolicy(id));
    }
}
