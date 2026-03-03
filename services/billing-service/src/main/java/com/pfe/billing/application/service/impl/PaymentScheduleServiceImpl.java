package com.pfe.billing.application.service.impl;

import com.pfe.billing.application.dto.PaymentScheduleDto;
import com.pfe.billing.application.mapper.PaymentScheduleMapper;
import com.pfe.billing.application.service.PaymentScheduleService;
import com.pfe.billing.domain.model.PaymentSchedule;
import com.pfe.billing.domain.repository.PaymentScheduleRepository;
import com.pfe.commons.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentScheduleServiceImpl implements PaymentScheduleService {

    private final PaymentScheduleRepository scheduleRepository;
    private final PaymentScheduleMapper scheduleMapper;

    @Override
    @Transactional
    public PaymentScheduleDto createSchedule(PaymentScheduleDto dto) {
        PaymentSchedule schedule = scheduleMapper.toDomain(dto);
        PaymentSchedule saved = scheduleRepository.save(schedule);
        return scheduleMapper.toDto(saved);
    }

    @Override
    public PaymentScheduleDto getScheduleByPolicyId(UUID policyId) {
        PaymentSchedule schedule = scheduleRepository.findByPolicyId(policyId)
                .orElseThrow(() -> new ResourceNotFoundException("PaymentSchedule", "policyId", policyId));
        return scheduleMapper.toDto(schedule);
    }

    @Override
    public List<PaymentScheduleDto> getAllSchedules() {
        return scheduleRepository.findAll().stream()
                .map(scheduleMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PaymentScheduleDto updateSchedule(UUID id, PaymentScheduleDto dto) {
        PaymentSchedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PaymentSchedule", "id", id));

        if (dto.getFrequency() != null)
            schedule.setFrequency(dto.getFrequency());
        if (dto.getNextDueDate() != null)
            schedule.setNextDueDate(dto.getNextDueDate());
        if (dto.getAmount() != null)
            schedule.setAmount(dto.getAmount());

        PaymentSchedule saved = scheduleRepository.save(schedule);
        return scheduleMapper.toDto(saved);
    }

    @Override
    @Transactional
    public void deleteSchedule(UUID id) {
        if (scheduleRepository.findById(id).isEmpty()) {
            throw new ResourceNotFoundException("PaymentSchedule", "id", id);
        }
        scheduleRepository.deleteById(id);
    }
}
