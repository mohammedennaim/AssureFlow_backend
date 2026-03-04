package com.pfe.billing.infrastructure.persistence.repository;

import com.pfe.billing.infrastructure.persistence.entity.PaymentScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface JpaPaymentScheduleRepository extends JpaRepository<PaymentScheduleEntity, UUID> {

    Optional<PaymentScheduleEntity> findByPolicyId(UUID policyId);
}
