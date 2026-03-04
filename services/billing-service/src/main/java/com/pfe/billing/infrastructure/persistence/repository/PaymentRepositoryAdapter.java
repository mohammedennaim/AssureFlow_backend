package com.pfe.billing.infrastructure.persistence.repository;

import com.pfe.billing.domain.model.Payment;
import com.pfe.billing.domain.repository.PaymentRepository;
import com.pfe.billing.infrastructure.persistence.mapper.PaymentEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PaymentRepositoryAdapter implements PaymentRepository {

    private final JpaPaymentRepository jpaPaymentRepository;
    private final PaymentEntityMapper mapper;

    @Override
    public Payment save(Payment payment) {
        var entity = mapper.toEntity(payment);
        var saved = jpaPaymentRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Payment> findById(UUID id) {
        return jpaPaymentRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Payment> findByInvoiceId(UUID invoiceId) {
        return jpaPaymentRepository.findByInvoiceId(invoiceId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Payment> findByClientId(UUID clientId) {
        return jpaPaymentRepository.findByClientId(clientId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Payment> findAll() {
        return jpaPaymentRepository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        jpaPaymentRepository.deleteById(id);
    }
}
