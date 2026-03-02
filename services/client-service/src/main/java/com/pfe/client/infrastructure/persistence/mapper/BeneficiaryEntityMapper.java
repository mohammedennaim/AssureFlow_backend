package com.pfe.client.infrastructure.persistence.mapper;

import com.pfe.client.domain.model.Beneficiary;
import com.pfe.client.infrastructure.persistence.entity.BeneficiaryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface BeneficiaryEntityMapper {
    BeneficiaryEntityMapper INSTANCE = Mappers.getMapper(BeneficiaryEntityMapper.class);

    BeneficiaryEntity toEntity(Beneficiary beneficiary);

    Beneficiary toDomain(BeneficiaryEntity entity);
}
