package com.pfe.claims.infrastructure.web.controller;

import com.pfe.claims.application.dto.*;
import com.pfe.claims.application.service.ClaimService;
import com.pfe.claims.domain.model.ClaimStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/claims")
@RequiredArgsConstructor
@Tag(name = "Claims API", description = "Endpoints for managing insurance claims")
public class ClaimController {

    private final ClaimService claimService;

    @PostMapping
    @Operation(summary = "Submit a new claim")
    public ResponseEntity<ClaimResponse> submitClaim(@Valid @RequestBody ClaimRequest request) {
        return new ResponseEntity<>(claimService.submitClaim(request), HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get all claims")
    public ResponseEntity<List<ClaimResponse>> getAllClaims() {
        return ResponseEntity.ok(claimService.getAllClaims());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get claim by ID")
    public ResponseEntity<ClaimResponse> getClaimById(@PathVariable String id) {
        return ResponseEntity.ok(claimService.getClaimById(id));
    }

    @GetMapping("/client/{clientId}")
    @Operation(summary = "Get claims by client ID")
    public ResponseEntity<List<ClaimResponse>> getClaimsByClientId(@PathVariable String clientId) {
        return ResponseEntity.ok(claimService.getClaimsByClientId(clientId));
    }

    @GetMapping("/policy/{policyId}")
    @Operation(summary = "Get claims by policy ID")
    public ResponseEntity<List<ClaimResponse>> getClaimsByPolicyId(@PathVariable String policyId) {
        return ResponseEntity.ok(claimService.getClaimsByPolicyId(policyId));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get claims by status")
    public ResponseEntity<List<ClaimResponse>> getClaimsByStatus(@PathVariable ClaimStatus status) {
        return ResponseEntity.ok(claimService.getClaimsByStatus(status));
    }

    @PatchMapping("/{id}/review")
    @Operation(summary = "Start review of a claim")
    public ResponseEntity<ClaimResponse> startReview(@PathVariable String id) {
        return ResponseEntity.ok(claimService.startReview(id));
    }

    @PatchMapping("/{id}/approve")
    @Operation(summary = "Approve a claim")
    public ResponseEntity<ClaimResponse> approveClaim(
            @PathVariable String id,
            @Valid @RequestBody ClaimApprovalRequest request) {
        return ResponseEntity.ok(claimService.approveClaim(id, request));
    }

    @PatchMapping("/{id}/reject")
    @Operation(summary = "Reject a claim")
    public ResponseEntity<ClaimResponse> rejectClaim(@PathVariable String id) {
        return ResponseEntity.ok(claimService.rejectClaim(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a claim")
    public ResponseEntity<Void> deleteClaim(@PathVariable String id) {
        claimService.deleteClaim(id);
        return ResponseEntity.noContent().build();
    }
}
