package com.pfe.billing.infrastructure.persistence.mapper;

import com.pfe.billing.domain.model.PaymentSchedule;
import com.pfe.billing.infrastructure.persistence.entity.PaymentScheduleEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PaymentScheduleEntityMapper {

    PaymentSchedule toDomain(PaymentScheduleEntity entity);

    PaymentScheduleEntity toEntity(PaymentSchedule domain);
}
