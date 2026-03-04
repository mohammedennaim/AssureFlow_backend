package com.pfe.billing.infrastructure.web.controller;

import com.pfe.billing.application.dto.CreateInvoiceRequest;
import com.pfe.billing.application.dto.InvoiceDto;
import com.pfe.billing.application.service.InvoiceService;
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
@RequestMapping("/api/v1/invoices")
@RequiredArgsConstructor
@Tag(name = "Invoices API", description = "Endpoints for managing invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;

    @PostMapping
    @Operation(summary = "Create a new invoice")
    public ResponseEntity<BaseResponse<InvoiceDto>> createInvoice(@Valid @RequestBody CreateInvoiceRequest request) {
        InvoiceDto response = invoiceService.createInvoice(request);
        return new ResponseEntity<>(BaseResponse.success(response), HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get all invoices")
    public ResponseEntity<BaseResponse<List<InvoiceDto>>> getAllInvoices() {
        return ResponseEntity.ok(BaseResponse.success(invoiceService.getAllInvoices()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get an invoice by ID")
    public ResponseEntity<BaseResponse<InvoiceDto>> getInvoiceById(@PathVariable UUID id) {
        return ResponseEntity.ok(BaseResponse.success(invoiceService.getInvoiceById(id)));
    }

    @GetMapping("/number/{invoiceNumber}")
    @Operation(summary = "Get an invoice by invoice number")
    public ResponseEntity<BaseResponse<InvoiceDto>> getInvoiceByNumber(@PathVariable String invoiceNumber) {
        return ResponseEntity.ok(BaseResponse.success(invoiceService.getInvoiceByNumber(invoiceNumber)));
    }

    @GetMapping("/client/{clientId}")
    @Operation(summary = "Get invoices by client ID")
    public ResponseEntity<BaseResponse<List<InvoiceDto>>> getInvoicesByClientId(@PathVariable UUID clientId) {
        return ResponseEntity.ok(BaseResponse.success(invoiceService.getInvoicesByClientId(clientId)));
    }

    @GetMapping("/policy/{policyId}")
    @Operation(summary = "Get invoices by policy ID")
    public ResponseEntity<BaseResponse<List<InvoiceDto>>> getInvoicesByPolicyId(@PathVariable UUID policyId) {
        return ResponseEntity.ok(BaseResponse.success(invoiceService.getInvoicesByPolicyId(policyId)));
    }

    @PatchMapping("/{id}/cancel")
    @Operation(summary = "Cancel an invoice")
    public ResponseEntity<Void> cancelInvoice(@PathVariable UUID id) {
        invoiceService.cancelInvoice(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{invoiceId}/pay/{paymentId}")
    @Operation(summary = "Mark an invoice as paid")
    public ResponseEntity<Void> markAsPaid(@PathVariable UUID invoiceId, @PathVariable UUID paymentId) {
        invoiceService.markAsPaid(invoiceId, paymentId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an invoice")
    public ResponseEntity<Void> deleteInvoice(@PathVariable UUID id) {
        invoiceService.deleteInvoice(id);
        return ResponseEntity.noContent().build();
    }
}
