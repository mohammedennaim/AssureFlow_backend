package com.pfe.billing.domain.repository;

import com.pfe.billing.domain.model.Invoice;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InvoiceRepository {

    Invoice save(Invoice invoice);

    Optional<Invoice> findById(UUID id);

    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);

    List<Invoice> findByClientId(UUID clientId);

    List<Invoice> findByPolicyId(UUID policyId);

    List<Invoice> findAll();

    void deleteById(UUID id);
}
