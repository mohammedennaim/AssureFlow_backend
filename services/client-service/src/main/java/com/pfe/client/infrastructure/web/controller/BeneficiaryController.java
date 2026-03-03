package com.pfe.client.infrastructure.web.controller;

import com.pfe.client.application.dto.BeneficiaryRequest;
import com.pfe.client.application.dto.BeneficiaryResponse;
import com.pfe.client.application.service.BeneficiaryService;
import com.pfe.commons.dto.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/clients/{clientId}/beneficiaries")
@RequiredArgsConstructor
@Tag(name = "Beneficiaries API", description = "Manage client beneficiaries")
public class BeneficiaryController {

    private final BeneficiaryService beneficiaryService;

    @PostMapping
    @Operation(summary = "Add beneficiary to client")
    public ResponseEntity<BaseResponse<BeneficiaryResponse>> create(@PathVariable UUID clientId,
            @Valid @RequestBody BeneficiaryRequest request) {
        var r = beneficiaryService.createBeneficiary(clientId, request);
        return ResponseEntity.ok(BaseResponse.success(r));
    }

    @GetMapping
    @Operation(summary = "List beneficiaries for a client")
    public ResponseEntity<BaseResponse<List<BeneficiaryResponse>>> list(@PathVariable UUID clientId) {
        return ResponseEntity.ok(BaseResponse.success(beneficiaryService.getBeneficiariesByClientId(clientId)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<BeneficiaryResponse>> getById(@PathVariable UUID clientId,
            @PathVariable UUID id) {
        return ResponseEntity.ok(BaseResponse.success(beneficiaryService.getBeneficiaryById(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable UUID clientId, @PathVariable UUID id) {
        beneficiaryService.deleteBeneficiary(id);
        return ResponseEntity.ok(BaseResponse.success(null, "Deleted"));
    }
}
