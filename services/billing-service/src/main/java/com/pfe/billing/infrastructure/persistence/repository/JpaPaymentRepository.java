package com.pfe.billing.infrastructure.persistence.repository;

import com.pfe.billing.infrastructure.persistence.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JpaPaymentRepository extends JpaRepository<PaymentEntity, UUID> {

    List<PaymentEntity> findByInvoiceId(UUID invoiceId);

    List<PaymentEntity> findByClientId(UUID clientId);
}
