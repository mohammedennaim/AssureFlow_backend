package com.pfe.billing.application.mapper;

import com.pfe.billing.application.dto.CreatePaymentRequest;
import com.pfe.billing.application.dto.PaymentDto;
import com.pfe.billing.domain.model.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PaymentMapper {

    PaymentDto toDto(Payment payment);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    Payment toDomain(CreatePaymentRequest request);
}
