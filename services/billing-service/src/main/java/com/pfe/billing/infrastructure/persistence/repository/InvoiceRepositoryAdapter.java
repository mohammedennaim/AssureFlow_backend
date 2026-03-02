package com.pfe.billing.infrastructure.persistence.repository;

import com.pfe.billing.domain.model.Invoice;
import com.pfe.billing.domain.model.InvoiceStatus;
import com.pfe.billing.domain.model.Payment;
import com.pfe.billing.domain.repository.InvoiceRepository;
import com.pfe.billing.infrastructure.persistence.entity.InvoiceEntity;
import com.pfe.billing.infrastructure.persistence.entity.PaymentEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class InvoiceRepositoryAdapter implements InvoiceRepository {

    private final JpaInvoiceRepository jpaInvoiceRepository;

    @Override
    public Invoice save(Invoice invoice) {
        InvoiceEntity entity = toEntity(invoice);
        if (entity.getPayments() != null) {
            entity.getPayments().forEach(p -> p.setInvoice(entity));
        }
        return toDomain(jpaInvoiceRepository.save(entity));
    }

    @Override
    public Optional<Invoice> findById(String id) {
        return jpaInvoiceRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Invoice> findByInvoiceNumber(String invoiceNumber) {
        return jpaInvoiceRepository.findByInvoiceNumber(invoiceNumber).map(this::toDomain);
    }

    @Override
    public List<Invoice> findByClientId(String clientId) {
        return jpaInvoiceRepository.findByClientId(clientId).stream()
                .map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Invoice> findByPolicyId(String policyId) {
        return jpaInvoiceRepository.findByPolicyId(policyId).stream()
                .map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Invoice> findByStatus(InvoiceStatus status) {
        return jpaInvoiceRepository.findByStatus(status).stream()
                .map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Invoice> findAll() {
        return jpaInvoiceRepository.findAll().stream()
                .map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        jpaInvoiceRepository.deleteById(id);
    }

    @Override
    public boolean existsByInvoiceNumber(String invoiceNumber) {
        return jpaInvoiceRepository.existsByInvoiceNumber(invoiceNumber);
    }

    private Invoice toDomain(InvoiceEntity entity) {
        List<Payment> payments = entity.getPayments() == null ? List.of() :
                entity.getPayments().stream().map(this::toPaymentDomain).collect(Collectors.toList());
        return Invoice.builder()
                .id(entity.getId())
                .invoiceNumber(entity.getInvoiceNumber())
                .policyId(entity.getPolicyId())
                .clientId(entity.getClientId())
                .amount(entity.getAmount())
                .dueDate(entity.getDueDate())
                .status(entity.getStatus())
                .payments(payments)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private InvoiceEntity toEntity(Invoice domain) {
        List<PaymentEntity> payments = domain.getPayments() == null ? List.of() :
                domain.getPayments().stream().map(this::toPaymentEntity).collect(Collectors.toList());
        return InvoiceEntity.builder()
                .id(domain.getId())
                .invoiceNumber(domain.getInvoiceNumber())
                .policyId(domain.getPolicyId())
                .clientId(domain.getClientId())
                .amount(domain.getAmount())
                .dueDate(domain.getDueDate())
                .status(domain.getStatus())
                .payments(payments)
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }

    private Payment toPaymentDomain(PaymentEntity entity) {
        return Payment.builder()
                .id(entity.getId())
                .invoiceId(entity.getInvoice() != null ? entity.getInvoice().getId() : null)
                .amount(entity.getAmount())
                .paymentDate(entity.getPaymentDate())
                .method(entity.getMethod())
                .reference(entity.getReference())
                .build();
    }

    private PaymentEntity toPaymentEntity(Payment domain) {
        return PaymentEntity.builder()
                .id(domain.getId())
                .amount(domain.getAmount())
                .paymentDate(domain.getPaymentDate())
                .method(domain.getMethod())
                .reference(domain.getReference())
                .build();
    }
}
