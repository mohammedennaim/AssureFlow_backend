package com.pfe.billing.domain.repository;

import com.pfe.billing.domain.model.Invoice;
import com.pfe.billing.domain.model.InvoiceStatus;

import java.util.List;
import java.util.Optional;

public interface InvoiceRepository {
    Invoice save(Invoice invoice);

    Optional<Invoice> findById(String id);

    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);

    List<Invoice> findByClientId(String clientId);

    List<Invoice> findByPolicyId(String policyId);

    List<Invoice> findByStatus(InvoiceStatus status);

    List<Invoice> findAll();

    void deleteById(String id);

    boolean existsByInvoiceNumber(String invoiceNumber);
}
