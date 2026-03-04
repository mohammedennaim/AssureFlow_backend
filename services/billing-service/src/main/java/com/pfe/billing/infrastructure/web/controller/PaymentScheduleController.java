package com.pfe.billing.infrastructure.web.controller;

import com.pfe.billing.application.dto.PaymentScheduleDto;
import com.pfe.billing.application.service.PaymentScheduleService;
import com.pfe.commons.dto.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payment-schedules")
@RequiredArgsConstructor
@Tag(name = "Payment Schedules API", description = "Endpoints for managing payment schedules")
public class PaymentScheduleController {

    private final PaymentScheduleService paymentScheduleService;

    @PostMapping
    @Operation(summary = "Create a payment schedule")
    public ResponseEntity<BaseResponse<PaymentScheduleDto>> createSchedule(@Valid @RequestBody PaymentScheduleDto dto) {
        PaymentScheduleDto response = paymentScheduleService.createSchedule(dto);
        return new ResponseEntity<>(BaseResponse.success(response), HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get all payment schedules")
    public ResponseEntity<BaseResponse<List<PaymentScheduleDto>>> getAllSchedules() {
        return ResponseEntity.ok(BaseResponse.success(paymentScheduleService.getAllSchedules()));
    }

    @GetMapping("/policy/{policyId}")
    @Operation(summary = "Get a payment schedule by policy ID")
    public ResponseEntity<BaseResponse<PaymentScheduleDto>> getScheduleByPolicyId(@PathVariable UUID policyId) {
        return ResponseEntity.ok(BaseResponse.success(paymentScheduleService.getScheduleByPolicyId(policyId)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a payment schedule")
    public ResponseEntity<BaseResponse<PaymentScheduleDto>> updateSchedule(@PathVariable UUID id,
            @Valid @RequestBody PaymentScheduleDto dto) {
        return ResponseEntity.ok(BaseResponse.success(paymentScheduleService.updateSchedule(id, dto)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a payment schedule")
    public ResponseEntity<Void> deleteSchedule(@PathVariable UUID id) {
        paymentScheduleService.deleteSchedule(id);
        return ResponseEntity.noContent().build();
    }
}
