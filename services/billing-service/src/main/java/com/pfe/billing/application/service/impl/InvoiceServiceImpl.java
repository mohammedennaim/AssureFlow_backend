package com.pfe.billing.application.service.impl;

import com.pfe.billing.application.dto.CreateInvoiceRequest;
import com.pfe.billing.application.dto.InvoiceDto;
import com.pfe.billing.application.mapper.InvoiceMapper;
import com.pfe.billing.application.service.InvoiceService;
import com.pfe.billing.domain.exception.InvoiceNotFoundException;
import com.pfe.billing.domain.model.Invoice;
import com.pfe.billing.domain.model.InvoiceStatus;
import com.pfe.billing.domain.repository.InvoiceRepository;
import com.pfe.billing.domain.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;
    private final InvoiceMapper invoiceMapper;

    @Override
    @Transactional
    public InvoiceDto createInvoice(CreateInvoiceRequest request) {
        Invoice invoice = invoiceMapper.toDomain(request);
        invoice.setInvoiceNumber("INV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        invoice.setStatus(InvoiceStatus.DRAFT);

        BigDecimal tax = request.getTaxAmount() != null ? request.getTaxAmount() : BigDecimal.ZERO;
        invoice.setTaxAmount(tax);
        invoice.setTotalAmount(request.getAmount().add(tax));

        Invoice saved = invoiceRepository.save(invoice);
        return invoiceMapper.toDto(saved);
    }

    @Override
    public InvoiceDto getInvoiceById(UUID id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new InvoiceNotFoundException(id));
        return invoiceMapper.toDto(invoice);
    }

    @Override
    public InvoiceDto getInvoiceByNumber(String invoiceNumber) {
        Invoice invoice = invoiceRepository.findByInvoiceNumber(invoiceNumber)
                .orElseThrow(() -> new InvoiceNotFoundException(invoiceNumber));
        return invoiceMapper.toDto(invoice);
    }

    @Override
    public List<InvoiceDto> getInvoicesByClientId(UUID clientId) {
        return invoiceRepository.findByClientId(clientId).stream()
                .map(invoiceMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<InvoiceDto> getInvoicesByPolicyId(UUID policyId) {
        return invoiceRepository.findByPolicyId(policyId).stream()
                .map(invoiceMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<InvoiceDto> getAllInvoices() {
        return invoiceRepository.findAll().stream()
                .map(invoiceMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void cancelInvoice(UUID id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new InvoiceNotFoundException(id));
        invoice.cancel();
        invoiceRepository.save(invoice);
    }

    @Override
    @Transactional
    public void markAsPaid(UUID invoiceId, UUID paymentId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new InvoiceNotFoundException(invoiceId));
        var payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new com.pfe.billing.domain.exception.PaymentNotFoundException(paymentId));
        invoice.markAsPaid(payment);
        invoiceRepository.save(invoice);
    }

    @Override
    @Transactional
    public void deleteInvoice(UUID id) {
        if (invoiceRepository.findById(id).isEmpty()) {
            throw new InvoiceNotFoundException(id);
        }
        invoiceRepository.deleteById(id);
    }
}
