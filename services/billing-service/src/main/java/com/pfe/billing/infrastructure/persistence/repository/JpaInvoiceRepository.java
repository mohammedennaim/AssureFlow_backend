package com.pfe.billing.infrastructure.persistence.repository;

import com.pfe.billing.domain.model.InvoiceStatus;
import com.pfe.billing.infrastructure.persistence.entity.InvoiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaInvoiceRepository extends JpaRepository<InvoiceEntity, String> {
    Optional<InvoiceEntity> findByInvoiceNumber(String invoiceNumber);

    List<InvoiceEntity> findByClientId(String clientId);

    List<InvoiceEntity> findByPolicyId(String policyId);

    List<InvoiceEntity> findByStatus(InvoiceStatus status);

    boolean existsByInvoiceNumber(String invoiceNumber);
}
