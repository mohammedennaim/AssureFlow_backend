package com.pfe.billing.domain.repository;

import com.pfe.billing.domain.model.PaymentSchedule;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentScheduleRepository {

    PaymentSchedule save(PaymentSchedule schedule);

    Optional<PaymentSchedule> findById(UUID id);

    Optional<PaymentSchedule> findByPolicyId(UUID policyId);

    List<PaymentSchedule> findAll();

    void deleteById(UUID id);
}
