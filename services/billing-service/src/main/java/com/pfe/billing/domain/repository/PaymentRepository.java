package com.pfe.billing.domain.repository;

import com.pfe.billing.domain.model.Payment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository {

    Payment save(Payment payment);

    Optional<Payment> findById(UUID id);

    List<Payment> findByInvoiceId(UUID invoiceId);

    List<Payment> findByClientId(UUID clientId);

    List<Payment> findAll();

    void deleteById(UUID id);
}
