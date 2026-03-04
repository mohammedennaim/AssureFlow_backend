package com.pfe.billing.infrastructure.persistence.mapper;

import com.pfe.billing.domain.model.Payment;
import com.pfe.billing.infrastructure.persistence.entity.PaymentEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentEntityMapper {

    Payment toDomain(PaymentEntity entity);

    @Mapping(target = "createdAt", ignore = true)
    PaymentEntity toEntity(Payment domain);
}
