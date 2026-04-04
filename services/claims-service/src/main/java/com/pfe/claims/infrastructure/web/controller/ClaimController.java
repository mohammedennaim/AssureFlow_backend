package com.pfe.claims.infrastructure.web.controller;

import com.pfe.claims.application.dto.ClaimDto;
import com.pfe.claims.application.dto.CreateClaimRequest;
import com.pfe.claims.application.dto.UpdateClaimRequest;
import com.pfe.claims.application.service.ClaimService;
import com.pfe.commons.dto.BaseResponse;
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
        public ResponseEntity<BaseResponse<ClaimDto>> createClaim(@Valid @RequestBody CreateClaimRequest request) {
                ClaimDto claim = claimService.createClaim(request);
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(BaseResponse.success(claim, "Claim created successfully"));
        }

        @Operation(summary = "Get a claim by ID", description = "Retrieves a specific claim using its unique ID")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Claim found"),
                        @ApiResponse(responseCode = "404", description = "Claim not found")
        })
        @GetMapping("/{id}")
        public ResponseEntity<BaseResponse<ClaimDto>> getClaimById(
                        @Parameter(description = "The unique ID of the claim") @PathVariable UUID id) {
                ClaimDto claim = claimService.getClaimById(id);
                return ResponseEntity.ok(BaseResponse.success(claim, "Claim retrieved successfully"));
        }

        @Operation(summary = "Get claims by Client ID", description = "Retrieves all claims associated with a specific client")
        @ApiResponse(responseCode = "200", description = "List of claims retrieved successfully")
        @GetMapping("/client/{clientId}")
        public ResponseEntity<BaseResponse<List<ClaimDto>>> getClaimsByClientId(
                        @Parameter(description = "The unique ID of the client") @PathVariable UUID clientId) {
                List<ClaimDto> claims = claimService.getClaimsByClientId(clientId);
                return ResponseEntity.ok(BaseResponse.success(claims, "Claims retrieved successfully"));
        }

        @Operation(summary = "Get claims by Policy ID", description = "Retrieves all claims associated with a specific policy")
        @ApiResponse(responseCode = "200", description = "List of claims retrieved successfully")
        @GetMapping("/policy/{policyId}")
        public ResponseEntity<BaseResponse<List<ClaimDto>>> getClaimsByPolicyId(
                        @Parameter(description = "The unique ID of the policy") @PathVariable UUID policyId) {
                List<ClaimDto> claims = claimService.getClaimsByPolicyId(policyId);
                return ResponseEntity.ok(BaseResponse.success(claims, "Claims retrieved successfully"));
        }

        @Operation(summary = "Get all claims", description = "Retrieves all claims in the system")
        @ApiResponse(responseCode = "200", description = "List of all claims retrieved successfully")
        @GetMapping
        public ResponseEntity<BaseResponse<List<ClaimDto>>> getAllClaims() {
                List<ClaimDto> claims = claimService.getAllClaims();
                return ResponseEntity.ok(BaseResponse.success(claims, "Claims retrieved successfully"));
        }

        @Operation(summary = "Update an existing claim", description = "Updates specific fields of an existing claim")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Claim updated successfully"),
                        @ApiResponse(responseCode = "404", description = "Claim not found")
        })
        @PatchMapping("/{id}")
        public ResponseEntity<BaseResponse<ClaimDto>> updateClaim(
                        @Parameter(description = "The unique ID of the claim to update") @PathVariable UUID id,
                        @RequestBody @Valid UpdateClaimRequest request) {
                ClaimDto claim = claimService.updateClaim(id, request);
                return ResponseEntity.ok(BaseResponse.success(claim, "Claim updated successfully"));
        }

        @Operation(summary = "Submit a claim", description = "Submits a claim for processing")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "Claim submitted successfully"),
                        @ApiResponse(responseCode = "400", description = "Claim cannot be submitted (invalid status)")
        })
        @PostMapping("/{id}/submit")
        public ResponseEntity<BaseResponse<Void>> submitClaim(
                        @Parameter(description = "The unique ID of the claim") @PathVariable UUID id) {
                claimService.submitClaim(id);
                return ResponseEntity.ok(BaseResponse.success(null, "Claim submitted successfully"));
        }

        @Operation(summary = "Review a claim", description = "Marks a submitted claim as under review")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "Claim marked as under review"),
                        @ApiResponse(responseCode = "400", description = "Claim cannot be reviewed (invalid status)")
        })
        @PostMapping("/{id}/review")
        public ResponseEntity<BaseResponse<Void>> reviewClaim(
                        @Parameter(description = "The unique ID of the claim") @PathVariable UUID id) {
                claimService.reviewClaim(id);
                return ResponseEntity.ok(BaseResponse.success(null, "Claim marked as under review"));
        }

        @Operation(summary = "Approve a claim", description = "Approves a claim under review with an approved amount")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "Claim approved successfully"),
                        @ApiResponse(responseCode = "400", description = "Claim cannot be approved (invalid status)")
        })
        @PostMapping("/{id}/approve")
        public ResponseEntity<BaseResponse<Void>> approveClaim(
                        @Parameter(description = "The unique ID of the claim") @PathVariable UUID id,
                        @Parameter(description = "Approved amount") @RequestParam BigDecimal amount,
                        @Parameter(description = "ID of the approver") @RequestParam UUID approvedBy) {
                claimService.approveClaim(id, amount, approvedBy);
                return ResponseEntity.ok(BaseResponse.success(null, "Claim approved successfully"));
        }

        @Operation(summary = "Reject a claim", description = "Rejects a claim under review with a reason")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "Claim rejected successfully"),
                        @ApiResponse(responseCode = "400", description = "Claim cannot be rejected (invalid status)")
        })
        @PostMapping("/{id}/reject")
        public ResponseEntity<BaseResponse<Void>> rejectClaim(
                        @Parameter(description = "The unique ID of the claim") @PathVariable UUID id,
                        @Parameter(description = "Reason for rejection") @RequestParam String reason) {
                claimService.rejectClaim(id, reason);
                return ResponseEntity.ok(BaseResponse.success(null, "Claim rejected successfully"));
        }

        @Operation(summary = "Request additional info", description = "Requests additional information for a claim")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "Info requested successfully"),
                        @ApiResponse(responseCode = "400", description = "Cannot request info (invalid status)")
        })
        @PostMapping("/{id}/request-info")
        public ResponseEntity<BaseResponse<Void>> requestInfo(
                        @Parameter(description = "The unique ID of the claim") @PathVariable UUID id) {
                claimService.requestInfo(id);
                return ResponseEntity.ok(BaseResponse.success(null, "Info requested successfully"));
        }

        @Operation(summary = "Mark claim as paid", description = "Marks an approved or payout-initiated claim as paid")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "Claim marked as paid successfully"),
                        @ApiResponse(responseCode = "400", description = "Claim cannot be marked as paid (invalid status)")
        })
        @PostMapping("/{id}/mark-as-paid")
        public ResponseEntity<BaseResponse<Void>> markAsPaid(
                        @Parameter(description = "The unique ID of the claim") @PathVariable UUID id) {
                claimService.markAsPaid(id);
                return ResponseEntity.ok(BaseResponse.success(null, "Claim marked as paid successfully"));
        }

        @Operation(summary = "Close a claim", description = "Closes a paid, rejected, or refunded claim")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "Claim closed successfully"),
                        @ApiResponse(responseCode = "400", description = "Claim cannot be closed (invalid status)")
        })
        @PostMapping("/{id}/close")
        public ResponseEntity<BaseResponse<Void>> closeClaim(
                        @Parameter(description = "The unique ID of the claim") @PathVariable UUID id) {
                claimService.closeClaim(id);
                return ResponseEntity.ok(BaseResponse.success(null, "Claim closed successfully"));
        }

        @Operation(summary = "Archive a claim", description = "Archives a claim from the admin dashboard while keeping it visible to the client")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "Claim archived successfully"),
                        @ApiResponse(responseCode = "404", description = "Claim not found")
        })
        @PostMapping("/{id}/archive")
        public ResponseEntity<BaseResponse<Void>> archiveClaim(
                        @Parameter(description = "The unique ID of the claim to archive") @PathVariable UUID id) {
                claimService.archiveClaim(id);
                return ResponseEntity.ok(BaseResponse.success(null, "Claim archived successfully"));
        }

}
