package com.pfe.billing.application.service.impl;

import com.pfe.billing.application.dto.*;
import com.pfe.billing.application.service.BillingService;
import com.pfe.billing.domain.exception.InvoiceNotFoundException;
import com.pfe.billing.domain.model.*;
import com.pfe.billing.domain.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BillingServiceImpl implements BillingService {

    private final InvoiceRepository invoiceRepository;

    @Override
    @Transactional
    public InvoiceResponse createInvoice(InvoiceRequest request) {
        log.info("Creating invoice for policy: {}", request.getPolicyId());
        String invoiceNumber = generateInvoiceNumber();
        while (invoiceRepository.existsByInvoiceNumber(invoiceNumber)) {
            invoiceNumber = generateInvoiceNumber();
        }

        Invoice invoice = Invoice.builder()
                .id(UUID.randomUUID().toString())
                .invoiceNumber(invoiceNumber)
                .policyId(request.getPolicyId())
                .clientId(request.getClientId())
                .amount(request.getAmount())
                .dueDate(request.getDueDate())
                .status(InvoiceStatus.PENDING)
                .build();

        return toResponse(invoiceRepository.save(invoice));
    }

    @Override
    @Transactional(readOnly = true)
    public InvoiceResponse getInvoiceById(String id) {
        return toResponse(invoiceRepository.findById(id)
                .orElseThrow(() -> new InvoiceNotFoundException(id)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvoiceResponse> getInvoicesByClientId(String clientId) {
        return invoiceRepository.findByClientId(clientId).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvoiceResponse> getInvoicesByPolicyId(String policyId) {
        return invoiceRepository.findByPolicyId(policyId).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvoiceResponse> getInvoicesByStatus(InvoiceStatus status) {
        return invoiceRepository.findByStatus(status).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvoiceResponse> getAllInvoices() {
        return invoiceRepository.findAll().stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PaymentResponse addPayment(PaymentRequest request) {
        log.info("Adding payment for invoice: {}", request.getInvoiceId());
        Invoice invoice = invoiceRepository.findById(request.getInvoiceId())
                .orElseThrow(() -> new InvoiceNotFoundException(request.getInvoiceId()));

        Payment payment = Payment.builder()
                .id(UUID.randomUUID().toString())
                .invoiceId(invoice.getId())
                .amount(request.getAmount())
                .paymentDate(request.getPaymentDate())
                .method(PaymentMethod.valueOf(request.getMethod()))
                .reference(request.getReference())
                .build();

        invoice.getPayments().add(payment);
        if (invoice.getRemainingBalance().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            invoice.markAsPaid();
        }

        invoiceRepository.save(invoice);
        return toPaymentResponse(payment);
    }

    @Override
    @Transactional
    public InvoiceResponse markInvoiceAsOverdue(String id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new InvoiceNotFoundException(id));
        invoice.markAsOverdue();
        return toResponse(invoiceRepository.save(invoice));
    }

    @Override
    @Transactional
    public void deleteInvoice(String id) {
        if (invoiceRepository.findById(id).isEmpty()) {
            throw new InvoiceNotFoundException(id);
        }
        invoiceRepository.deleteById(id);
    }

    private String generateInvoiceNumber() {
        return "INV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private InvoiceResponse toResponse(Invoice invoice) {
        List<PaymentResponse> payments = invoice.getPayments() == null ? List.of() :
                invoice.getPayments().stream().map(this::toPaymentResponse).collect(Collectors.toList());
        return InvoiceResponse.builder()
                .id(invoice.getId())
                .invoiceNumber(invoice.getInvoiceNumber())
                .policyId(invoice.getPolicyId())
                .clientId(invoice.getClientId())
                .amount(invoice.getAmount())
                .remainingBalance(invoice.getRemainingBalance())
                .dueDate(invoice.getDueDate())
                .status(invoice.getStatus() != null ? invoice.getStatus().name() : null)
                .payments(payments)
                .createdAt(invoice.getCreatedAt())
                .updatedAt(invoice.getUpdatedAt())
                .build();
    }

    private PaymentResponse toPaymentResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .invoiceId(payment.getInvoiceId())
                .amount(payment.getAmount())
                .paymentDate(payment.getPaymentDate())
                .method(payment.getMethod() != null ? payment.getMethod().name() : null)
                .reference(payment.getReference())
                .build();
    }
}
