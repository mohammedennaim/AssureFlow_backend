package com.pfe.billing.application.service.impl;

import com.pfe.billing.application.dto.CreatePaymentRequest;
import com.pfe.billing.application.dto.PaymentDto;
import com.pfe.billing.application.mapper.PaymentMapper;
import com.pfe.billing.application.service.PaymentService;
import com.pfe.billing.domain.exception.PaymentNotFoundException;
import com.pfe.billing.domain.model.Payment;
import com.pfe.billing.domain.event.PaymentReceivedEvent;
import com.pfe.billing.domain.model.PaymentStatus;
import com.pfe.billing.domain.repository.PaymentRepository;
import com.pfe.billing.infrastructure.messaging.BillingEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final BillingEventPublisher billingEventPublisher;

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    @Transactional
    public PaymentDto createPayment(CreatePaymentRequest request) {
        Payment payment = paymentMapper.toDomain(request);
        payment.setStatus(PaymentStatus.PENDING);
        Payment saved = paymentRepository.save(payment);

        billingEventPublisher.publishPaymentReceived(PaymentReceivedEvent.builder()
                .paymentId(saved.getId())
                .invoiceId(saved.getInvoiceId())
                .correlationId(UUID.randomUUID())
                .eventTimestamp(LocalDateTime.now())
                .build());

        return paymentMapper.toDto(saved);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT', 'CLIENT')")
    public PaymentDto getPaymentById(UUID id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException(id));
        return paymentMapper.toDto(payment);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT', 'CLIENT')")
    public List<PaymentDto> getPaymentsByInvoiceId(UUID invoiceId) {
        return paymentRepository.findByInvoiceId(invoiceId).stream()
                .map(paymentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT', 'CLIENT')")
    public List<PaymentDto> getPaymentsByClientId(UUID clientId) {
        return paymentRepository.findByClientId(clientId).stream()
                .map(paymentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    public List<PaymentDto> getAllPayments() {
        return paymentRepository.findAll().stream()
                .map(paymentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void deletePayment(UUID id) {
        if (paymentRepository.findById(id).isEmpty()) {
            throw new PaymentNotFoundException(id);
        }
        paymentRepository.deleteById(id);
    }
}
