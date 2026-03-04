package com.pfe.billing.infrastructure.persistence.repository;

import com.pfe.billing.infrastructure.persistence.entity.InvoiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaInvoiceRepository extends JpaRepository<InvoiceEntity, UUID> {

    Optional<InvoiceEntity> findByInvoiceNumber(String invoiceNumber);

    List<InvoiceEntity> findByClientId(UUID clientId);

    List<InvoiceEntity> findByPolicyId(UUID policyId);
}
