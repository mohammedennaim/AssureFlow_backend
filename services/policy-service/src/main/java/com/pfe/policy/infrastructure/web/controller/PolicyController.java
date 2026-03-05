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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
        public ResponseEntity<PolicyDto> createPolicy(@Valid @RequestBody CreatePolicyRequest request) {
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

        @Operation(summary = "Get all policies", description = "Retrieves all policies in the system (non-paginated)")
        @ApiResponse(responseCode = "200", description = "List of all policies retrieved successfully")
        @GetMapping
        public ResponseEntity<List<PolicyDto>> getAllPolicies() {
                return ResponseEntity.ok(policyService.getAllPolicies());
        }

        @Operation(summary = "Get all policies (paginated)", description = "Retrieves policies with pagination support")
        @ApiResponse(responseCode = "200", description = "Page of policies retrieved successfully")
        @GetMapping("/page")
        public ResponseEntity<Page<PolicyDto>> getAllPoliciesPaged(
                        @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
                return ResponseEntity.ok(policyService.getAllPolicies(pageable));
        }

        @Operation(summary = "Update an existing policy", description = "Updates specific fields of an existing policy")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Policy updated successfully"),
                        @ApiResponse(responseCode = "404", description = "Policy not found")
        })
        @PatchMapping("/{id}")
        public ResponseEntity<PolicyDto> updatePolicy(
                        @Parameter(description = "The unique ID of the policy to update") @PathVariable String id,
                        @RequestBody @Valid UpdatePolicyRequest request) {
                return ResponseEntity.ok(policyService.updatePolicy(id, request));
        }

        @Operation(summary = "Submit a policy", description = "Submits a DRAFT policy for activation")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "Policy submitted successfully"),
                        @ApiResponse(responseCode = "404", description = "Policy not found"),
                        @ApiResponse(responseCode = "400", description = "Policy cannot be submitted (invalid status)")
        })
        @PostMapping("/{id}/submit")
        public ResponseEntity<Void> submitPolicy(
                        @Parameter(description = "The unique ID of the policy to submit") @PathVariable String id) {
                policyService.submitPolicy(id);
                return ResponseEntity.noContent().build();
        }

        @Operation(summary = "Approve a policy", description = "Approves an active policy")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "Policy approved successfully"),
                        @ApiResponse(responseCode = "404", description = "Policy not found"),
                        @ApiResponse(responseCode = "400", description = "Policy cannot be approved (invalid status)")
        })
        @PostMapping("/{id}/approve")
        public ResponseEntity<Void> approvePolicy(
                        @Parameter(description = "The unique ID of the policy to approve") @PathVariable String id) {
                policyService.approvePolicy(id);
                return ResponseEntity.noContent().build();
        }

        @Operation(summary = "Reject a policy", description = "Rejects a policy with a reason")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "Policy rejected successfully"),
                        @ApiResponse(responseCode = "404", description = "Policy not found"),
                        @ApiResponse(responseCode = "400", description = "Policy cannot be rejected (invalid status)")
        })
        @PostMapping("/{id}/reject")
        public ResponseEntity<Void> rejectPolicy(
                        @Parameter(description = "The unique ID of the policy to reject") @PathVariable String id,
                        @Parameter(description = "Reason for rejection") @RequestParam String reason) {
                policyService.rejectPolicy(id, reason);
                return ResponseEntity.noContent().build();
        }

        @Operation(summary = "Cancel a policy", description = "Cancels a policy with a reason")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "Policy cancelled successfully"),
                        @ApiResponse(responseCode = "404", description = "Policy not found"),
                        @ApiResponse(responseCode = "400", description = "Policy is already cancelled")
        })
        @PostMapping("/{id}/cancel")
        public ResponseEntity<Void> cancelPolicy(
                        @Parameter(description = "The unique ID of the policy to cancel") @PathVariable String id,
                        @Parameter(description = "Reason for cancellation") @RequestParam String reason) {
                policyService.cancelPolicy(id, reason);
                return ResponseEntity.noContent().build();
        }

        @Operation(summary = "Expire a policy", description = "Marks an active policy as expired with a reason")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "Policy expired successfully"),
                        @ApiResponse(responseCode = "404", description = "Policy not found"),
                        @ApiResponse(responseCode = "400", description = "Policy cannot be expired (invalid status)")
        })
        @PostMapping("/{id}/expire")
        public ResponseEntity<Void> expirePolicy(
                        @Parameter(description = "The unique ID of the policy to expire") @PathVariable String id,
                        @Parameter(description = "Reason for expiration") @RequestParam String reason) {
                policyService.expirePolicy(id, reason);
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
