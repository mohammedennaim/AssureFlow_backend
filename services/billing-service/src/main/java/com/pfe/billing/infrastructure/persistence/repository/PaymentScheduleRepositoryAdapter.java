package com.pfe.billing.infrastructure.persistence.repository;

import com.pfe.billing.domain.model.PaymentSchedule;
import com.pfe.billing.domain.repository.PaymentScheduleRepository;
import com.pfe.billing.infrastructure.persistence.mapper.PaymentScheduleEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PaymentScheduleRepositoryAdapter implements PaymentScheduleRepository {

    private final JpaPaymentScheduleRepository jpaPaymentScheduleRepository;
    private final PaymentScheduleEntityMapper mapper;

    @Override
    public PaymentSchedule save(PaymentSchedule schedule) {
        var entity = mapper.toEntity(schedule);
        var saved = jpaPaymentScheduleRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<PaymentSchedule> findById(UUID id) {
        return jpaPaymentScheduleRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<PaymentSchedule> findByPolicyId(UUID policyId) {
        return jpaPaymentScheduleRepository.findByPolicyId(policyId).map(mapper::toDomain);
    }

    @Override
    public List<PaymentSchedule> findAll() {
        return jpaPaymentScheduleRepository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        jpaPaymentScheduleRepository.deleteById(id);
    }
}
