package com.pfe.billing.infrastructure.persistence.repository;

import com.pfe.billing.domain.model.Invoice;
import com.pfe.billing.domain.repository.InvoiceRepository;
import com.pfe.billing.infrastructure.persistence.entity.InvoiceEntity;
import com.pfe.billing.infrastructure.persistence.mapper.InvoiceEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class InvoiceRepositoryAdapter implements InvoiceRepository {

    private final JpaInvoiceRepository jpaInvoiceRepository;
    private final InvoiceEntityMapper mapper;

    @Override
    public Invoice save(Invoice invoice) {
        InvoiceEntity entity = mapper.toEntity(invoice);
        InvoiceEntity saved = jpaInvoiceRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Invoice> findById(UUID id) {
        return jpaInvoiceRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Invoice> findByInvoiceNumber(String invoiceNumber) {
        return jpaInvoiceRepository.findByInvoiceNumber(invoiceNumber).map(mapper::toDomain);
    }

    @Override
    public List<Invoice> findByClientId(UUID clientId) {
        return jpaInvoiceRepository.findByClientId(clientId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Invoice> findByPolicyId(UUID policyId) {
        return jpaInvoiceRepository.findByPolicyId(policyId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Invoice> findAll() {
        return jpaInvoiceRepository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Page<Invoice> findAllPaged(Pageable pageable) {
        return jpaInvoiceRepository.findAll(pageable).map(mapper::toDomain);
    }

    @Override
    public void deleteById(UUID id) {
        jpaInvoiceRepository.deleteById(id);
    }
}
