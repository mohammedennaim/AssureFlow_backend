package com.pfe.claims.infrastructure.web.controller;

import com.pfe.claims.application.dto.ClaimDto;
import com.pfe.claims.application.dto.CreateClaimRequest;
import com.pfe.claims.application.dto.UpdateClaimRequest;
import com.pfe.claims.application.service.ClaimService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/claims")
@RequiredArgsConstructor
@Tag(name = "Claims API", description = "Endpoints for managing insurance claims")
public class ClaimController {

        private final ClaimService claimService;

        @Operation(summary = "Create a new claim", description = "Creates a new insurance claim")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Claim created successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid input provided")
        })
        @PostMapping
        public ResponseEntity<ClaimDto> createClaim(@Valid @RequestBody CreateClaimRequest request) {
                return ResponseEntity.status(HttpStatus.CREATED).body(claimService.createClaim(request));
        }

        @Operation(summary = "Get a claim by ID", description = "Retrieves a specific claim using its unique ID")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Claim found"),
                        @ApiResponse(responseCode = "404", description = "Claim not found")
        })
        @GetMapping("/{id}")
        public ResponseEntity<ClaimDto> getClaimById(
                        @Parameter(description = "The unique ID of the claim") @PathVariable UUID id) {
                return ResponseEntity.ok(claimService.getClaimById(id));
        }

        @Operation(summary = "Get claims by Client ID", description = "Retrieves all claims associated with a specific client")
        @ApiResponse(responseCode = "200", description = "List of claims retrieved successfully")
        @GetMapping("/client/{clientId}")
        public ResponseEntity<List<ClaimDto>> getClaimsByClientId(
                        @Parameter(description = "The unique ID of the client") @PathVariable UUID clientId) {
                return ResponseEntity.ok(claimService.getClaimsByClientId(clientId));
        }

        @Operation(summary = "Get claims by Policy ID", description = "Retrieves all claims associated with a specific policy")
        @ApiResponse(responseCode = "200", description = "List of claims retrieved successfully")
        @GetMapping("/policy/{policyId}")
        public ResponseEntity<List<ClaimDto>> getClaimsByPolicyId(
                        @Parameter(description = "The unique ID of the policy") @PathVariable UUID policyId) {
                return ResponseEntity.ok(claimService.getClaimsByPolicyId(policyId));
        }

        @Operation(summary = "Get all claims", description = "Retrieves all claims in the system")
        @ApiResponse(responseCode = "200", description = "List of all claims retrieved successfully")
        @GetMapping
        public ResponseEntity<List<ClaimDto>> getAllClaims() {
                return ResponseEntity.ok(claimService.getAllClaims());
        }

        @Operation(summary = "Update an existing claim", description = "Updates specific fields of an existing claim")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Claim updated successfully"),
                        @ApiResponse(responseCode = "404", description = "Claim not found")
        })
        @PatchMapping("/{id}")
        public ResponseEntity<ClaimDto> updateClaim(
                        @Parameter(description = "The unique ID of the claim to update") @PathVariable UUID id,
                        @RequestBody @Valid UpdateClaimRequest request) {
                return ResponseEntity.ok(claimService.updateClaim(id, request));
        }

        @Operation(summary = "Submit a claim", description = "Submits a claim for processing")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "Claim submitted successfully"),
                        @ApiResponse(responseCode = "400", description = "Claim cannot be submitted (invalid status)")
        })
        @PostMapping("/{id}/submit")
        public ResponseEntity<Void> submitClaim(
                        @Parameter(description = "The unique ID of the claim") @PathVariable UUID id) {
                claimService.submitClaim(id);
                return ResponseEntity.noContent().build();
        }

        @Operation(summary = "Review a claim", description = "Marks a submitted claim as under review")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "Claim marked as under review"),
                        @ApiResponse(responseCode = "400", description = "Claim cannot be reviewed (invalid status)")
        })
        @PostMapping("/{id}/review")
        public ResponseEntity<Void> reviewClaim(
                        @Parameter(description = "The unique ID of the claim") @PathVariable UUID id) {
                claimService.reviewClaim(id);
                return ResponseEntity.noContent().build();
        }

        @Operation(summary = "Approve a claim", description = "Approves a claim under review with an approved amount")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "Claim approved successfully"),
                        @ApiResponse(responseCode = "400", description = "Claim cannot be approved (invalid status)")
        })
        @PostMapping("/{id}/approve")
        public ResponseEntity<Void> approveClaim(
                        @Parameter(description = "The unique ID of the claim") @PathVariable UUID id,
                        @Parameter(description = "Approved amount") @RequestParam BigDecimal amount,
                        @Parameter(description = "ID of the approver") @RequestParam UUID approvedBy) {
                claimService.approveClaim(id, amount, approvedBy);
                return ResponseEntity.noContent().build();
        }

        @Operation(summary = "Reject a claim", description = "Rejects a claim under review with a reason")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "Claim rejected successfully"),
                        @ApiResponse(responseCode = "400", description = "Claim cannot be rejected (invalid status)")
        })
        @PostMapping("/{id}/reject")
        public ResponseEntity<Void> rejectClaim(
                        @Parameter(description = "The unique ID of the claim") @PathVariable UUID id,
                        @Parameter(description = "Reason for rejection") @RequestParam String reason) {
                claimService.rejectClaim(id, reason);
                return ResponseEntity.noContent().build();
        }

        @Operation(summary = "Request additional info", description = "Requests additional information for a claim")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "Info requested successfully"),
                        @ApiResponse(responseCode = "400", description = "Cannot request info (invalid status)")
        })
        @PostMapping("/{id}/request-info")
        public ResponseEntity<Void> requestInfo(
                        @Parameter(description = "The unique ID of the claim") @PathVariable UUID id) {
                claimService.requestInfo(id);
                return ResponseEntity.noContent().build();
        }

        @Operation(summary = "Mark claim as paid", description = "Marks an approved or payout-initiated claim as paid")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "Claim marked as paid successfully"),
                        @ApiResponse(responseCode = "400", description = "Claim cannot be marked as paid (invalid status)")
        })
        @PostMapping("/{id}/mark-as-paid")
        public ResponseEntity<Void> markAsPaid(
                        @Parameter(description = "The unique ID of the claim") @PathVariable UUID id) {
                claimService.markAsPaid(id);
                return ResponseEntity.noContent().build();
        }

        @Operation(summary = "Close a claim", description = "Closes a paid, rejected, or refunded claim")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "Claim closed successfully"),
                        @ApiResponse(responseCode = "400", description = "Claim cannot be closed (invalid status)")
        })
        @PostMapping("/{id}/close")
        public ResponseEntity<Void> closeClaim(
                        @Parameter(description = "The unique ID of the claim") @PathVariable UUID id) {
                claimService.closeClaim(id);
                return ResponseEntity.noContent().build();
        }

        @Operation(summary = "Delete a claim", description = "Deletes a claim from the system")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "Claim deleted successfully"),
                        @ApiResponse(responseCode = "404", description = "Claim not found")
        })
        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deleteClaim(
                        @Parameter(description = "The unique ID of the claim to delete") @PathVariable UUID id) {
                claimService.deleteClaim(id);
                return ResponseEntity.noContent().build();
        }
}
