package com.pfe.billing.infrastructure.web.controller;

import com.pfe.billing.application.dto.*;
import com.pfe.billing.application.service.BillingService;
import com.pfe.billing.domain.model.InvoiceStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Billing API", description = "Endpoints for managing invoices and payments")
public class BillingController {

    private final BillingService billingService;

    @PostMapping("/api/v1/invoices")
    @Operation(summary = "Create a new invoice")
    public ResponseEntity<InvoiceResponse> createInvoice(@Valid @RequestBody InvoiceRequest request) {
        return new ResponseEntity<>(billingService.createInvoice(request), HttpStatus.CREATED);
    }

    @GetMapping("/api/v1/invoices")
    @Operation(summary = "Get all invoices")
    public ResponseEntity<List<InvoiceResponse>> getAllInvoices() {
        return ResponseEntity.ok(billingService.getAllInvoices());
    }

    @GetMapping("/api/v1/invoices/{id}")
    @Operation(summary = "Get invoice by ID")
    public ResponseEntity<InvoiceResponse> getInvoiceById(@PathVariable String id) {
        return ResponseEntity.ok(billingService.getInvoiceById(id));
    }

    @GetMapping("/api/v1/invoices/client/{clientId}")
    @Operation(summary = "Get invoices by client ID")
    public ResponseEntity<List<InvoiceResponse>> getInvoicesByClientId(@PathVariable String clientId) {
        return ResponseEntity.ok(billingService.getInvoicesByClientId(clientId));
    }

    @GetMapping("/api/v1/invoices/policy/{policyId}")
    @Operation(summary = "Get invoices by policy ID")
    public ResponseEntity<List<InvoiceResponse>> getInvoicesByPolicyId(@PathVariable String policyId) {
        return ResponseEntity.ok(billingService.getInvoicesByPolicyId(policyId));
    }

    @GetMapping("/api/v1/invoices/status/{status}")
    @Operation(summary = "Get invoices by status")
    public ResponseEntity<List<InvoiceResponse>> getInvoicesByStatus(@PathVariable InvoiceStatus status) {
        return ResponseEntity.ok(billingService.getInvoicesByStatus(status));
    }

    @PatchMapping("/api/v1/invoices/{id}/overdue")
    @Operation(summary = "Mark invoice as overdue")
    public ResponseEntity<InvoiceResponse> markAsOverdue(@PathVariable String id) {
        return ResponseEntity.ok(billingService.markInvoiceAsOverdue(id));
    }

    @DeleteMapping("/api/v1/invoices/{id}")
    @Operation(summary = "Delete an invoice")
    public ResponseEntity<Void> deleteInvoice(@PathVariable String id) {
        billingService.deleteInvoice(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/api/v1/payments")
    @Operation(summary = "Add a payment to an invoice")
    public ResponseEntity<PaymentResponse> addPayment(@Valid @RequestBody PaymentRequest request) {
        return new ResponseEntity<>(billingService.addPayment(request), HttpStatus.CREATED);
    }
}
