package com.pfe.billing.application.service;

import com.pfe.billing.application.dto.PaymentScheduleDto;

import java.util.List;
import java.util.UUID;

public interface PaymentScheduleService {

    PaymentScheduleDto createSchedule(PaymentScheduleDto dto);

    PaymentScheduleDto getScheduleByPolicyId(UUID policyId);

    List<PaymentScheduleDto> getAllSchedules();

    PaymentScheduleDto updateSchedule(UUID id, PaymentScheduleDto dto);

    void deleteSchedule(UUID id);
}
