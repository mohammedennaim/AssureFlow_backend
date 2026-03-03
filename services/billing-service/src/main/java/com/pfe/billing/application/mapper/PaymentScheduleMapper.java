package com.pfe.billing.application.mapper;

import com.pfe.billing.application.dto.PaymentScheduleDto;
import com.pfe.billing.domain.model.PaymentSchedule;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PaymentScheduleMapper {

    PaymentScheduleDto toDto(PaymentSchedule schedule);

    @Mapping(target = "id", ignore = true)
    PaymentSchedule toDomain(PaymentScheduleDto dto);
}
