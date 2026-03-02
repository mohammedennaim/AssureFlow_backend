package com.pfe.client.application.mapper;

import com.pfe.client.application.dto.BeneficiaryRequest;
import com.pfe.client.application.dto.BeneficiaryResponse;
import com.pfe.client.domain.model.Beneficiary;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface BeneficiaryMapper {
    BeneficiaryMapper INSTANCE = Mappers.getMapper(BeneficiaryMapper.class);

    Beneficiary toDomain(BeneficiaryRequest request);

    BeneficiaryResponse toResponse(Beneficiary beneficiary);
}
