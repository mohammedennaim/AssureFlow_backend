package com.pfe.billing.infrastructure.web.controller;

import com.pfe.billing.application.dto.CreatePaymentRequest;
import com.pfe.billing.application.dto.PaymentDto;
import com.pfe.billing.application.service.PaymentService;
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
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Tag(name = "Payments API", description = "Endpoints for managing payments")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    @Operation(summary = "Create a new payment")
    public ResponseEntity<BaseResponse<PaymentDto>> createPayment(@Valid @RequestBody CreatePaymentRequest request) {
        PaymentDto response = paymentService.createPayment(request);
        return new ResponseEntity<>(BaseResponse.success(response), HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get all payments")
    public ResponseEntity<BaseResponse<List<PaymentDto>>> getAllPayments() {
        return ResponseEntity.ok(BaseResponse.success(paymentService.getAllPayments()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a payment by ID")
    public ResponseEntity<BaseResponse<PaymentDto>> getPaymentById(@PathVariable UUID id) {
        return ResponseEntity.ok(BaseResponse.success(paymentService.getPaymentById(id)));
    }

    @GetMapping("/invoice/{invoiceId}")
    @Operation(summary = "Get payments by invoice ID")
    public ResponseEntity<BaseResponse<List<PaymentDto>>> getPaymentsByInvoiceId(@PathVariable UUID invoiceId) {
        return ResponseEntity.ok(BaseResponse.success(paymentService.getPaymentsByInvoiceId(invoiceId)));
    }

    @GetMapping("/client/{clientId}")
    @Operation(summary = "Get payments by client ID")
    public ResponseEntity<BaseResponse<List<PaymentDto>>> getPaymentsByClientId(@PathVariable UUID clientId) {
        return ResponseEntity.ok(BaseResponse.success(paymentService.getPaymentsByClientId(clientId)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a payment")
    public ResponseEntity<Void> deletePayment(@PathVariable UUID id) {
        paymentService.deletePayment(id);
        return ResponseEntity.noContent().build();
    }
}
