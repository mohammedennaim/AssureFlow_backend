package com.pfe.billing.application.service.impl;

import com.pfe.billing.application.dto.CreateInvoiceRequest;
import com.pfe.billing.application.dto.InvoiceDto;
import com.pfe.billing.application.mapper.InvoiceMapper;
import com.pfe.billing.application.service.InvoiceService;
import com.pfe.billing.domain.exception.InvoiceNotFoundException;
import com.pfe.billing.domain.model.Invoice;
import com.pfe.billing.domain.model.InvoiceStatus;
import com.pfe.billing.domain.event.InvoiceGeneratedEvent;
import com.pfe.billing.domain.event.PaymentReceivedEvent;
import com.pfe.billing.domain.repository.InvoiceRepository;
import com.pfe.billing.domain.repository.PaymentRepository;
import com.pfe.billing.infrastructure.client.PolicyDto;
import com.pfe.billing.infrastructure.client.PolicyServiceClient;
import com.pfe.billing.infrastructure.messaging.BillingEventPublisher;
import com.pfe.commons.exceptions.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;
    private final InvoiceMapper invoiceMapper;
    private final PolicyServiceClient policyServiceClient;
    private final BillingEventPublisher billingEventPublisher;

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    @Transactional
    public InvoiceDto createInvoice(CreateInvoiceRequest request) {
        validatePolicyExists(request.getPolicyId());

        Invoice invoice = invoiceMapper.toDomain(request);
        invoice.setInvoiceNumber("INV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        invoice.setStatus(InvoiceStatus.DRAFT);

        BigDecimal tax = request.getTaxAmount() != null ? request.getTaxAmount() : BigDecimal.ZERO;
        invoice.setTaxAmount(tax);
        invoice.setTotalAmount(request.getAmount().add(tax));

        Invoice saved = invoiceRepository.save(invoice);

        billingEventPublisher.publishInvoiceGenerated(InvoiceGeneratedEvent.builder()
                .invoiceId(saved.getId())
                .policyId(saved.getPolicyId())
                .correlationId(UUID.randomUUID())
                .eventTimestamp(LocalDateTime.now())
                .build());

        return invoiceMapper.toDto(saved);
    }

    private void validatePolicyExists(UUID policyId) {
        if (policyId == null) {
            throw new BusinessException("Policy ID is required");
        }
        try {
            PolicyDto policy = policyServiceClient.getPolicyById(policyId.toString());
            if (policy == null) {
                throw new BusinessException("Policy not found with ID: " + policyId);
            }
            log.info("Policy validated for invoice: {} (status: {})", policy.getPolicyNumber(), policy.getStatus());
        } catch (BusinessException e) {
            throw e;
        } catch (feign.FeignException e) {
            log.error("Feign call failed to validate policy {}: {}", policyId, e.getMessage());
            throw new BusinessException("Unable to reach policy-service: " + e.getMessage());
        }
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT', 'CLIENT')")
    @Transactional(readOnly = true)
    @Cacheable(value = "invoices", key = "#id")
    public InvoiceDto getInvoiceById(UUID id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new InvoiceNotFoundException(id));
        return invoiceMapper.toDto(invoice);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT', 'CLIENT')")
    @Transactional(readOnly = true)
    @Cacheable(value = "invoices", key = "'number:' + #invoiceNumber")
    public InvoiceDto getInvoiceByNumber(String invoiceNumber) {
        Invoice invoice = invoiceRepository.findByInvoiceNumber(invoiceNumber)
                .orElseThrow(() -> new InvoiceNotFoundException(invoiceNumber));
        return invoiceMapper.toDto(invoice);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT', 'CLIENT')")
    @Transactional(readOnly = true)
    @Cacheable(value = "invoices", key = "'client:' + #clientId")
    public List<InvoiceDto> getInvoicesByClientId(UUID clientId) {
        return invoiceRepository.findByClientId(clientId).stream()
                .map(invoiceMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT', 'CLIENT')")
    @Transactional(readOnly = true)
    @Cacheable(value = "invoices", key = "'policy:' + #policyId")
    public List<InvoiceDto> getInvoicesByPolicyId(UUID policyId) {
        return invoiceRepository.findByPolicyId(policyId).stream()
                .map(invoiceMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    @Transactional(readOnly = true)
    public List<InvoiceDto> getAllInvoices() {
        return invoiceRepository.findAll().stream()
                .map(invoiceMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    @Transactional(readOnly = true)
    public Page<InvoiceDto> getAllInvoicesPaged(int page, int size) {
        return invoiceRepository.findAllPaged(
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")))
                .map(invoiceMapper::toDto);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    @Transactional
    @CacheEvict(value = "invoices", key = "#id")
    public void cancelInvoice(UUID id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new InvoiceNotFoundException(id));
        invoice.cancel();
        invoiceRepository.save(invoice);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    @Transactional
    @CacheEvict(value = "invoices", key = "#invoiceId")
    public void markAsPaid(UUID invoiceId, UUID paymentId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new InvoiceNotFoundException(invoiceId));
        var payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new com.pfe.billing.domain.exception.PaymentNotFoundException(paymentId));
        invoice.markAsPaid(payment);
        invoiceRepository.save(invoice);

        billingEventPublisher.publishPaymentReceived(PaymentReceivedEvent.builder()
                .paymentId(paymentId)
                .invoiceId(invoiceId)
                .correlationId(UUID.randomUUID())
                .eventTimestamp(LocalDateTime.now())
                .build());
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    @CacheEvict(value = "invoices", allEntries = true)
    public void deleteInvoice(UUID id) {
        if (invoiceRepository.findById(id).isEmpty()) {
            throw new InvoiceNotFoundException(id);
        }
        invoiceRepository.deleteById(id);
    }
}
