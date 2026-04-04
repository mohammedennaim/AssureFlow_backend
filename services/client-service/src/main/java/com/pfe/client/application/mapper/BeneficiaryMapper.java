package com.pfe.client.application.mapper;

import com.pfe.client.application.dto.BeneficiaryRequest;
import com.pfe.client.application.dto.BeneficiaryResponse;
import com.pfe.client.domain.model.Beneficiary;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BeneficiaryMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "clientId", ignore = true)
    Beneficiary toDomain(BeneficiaryRequest request);

    BeneficiaryResponse toResponse(Beneficiary beneficiary);
}
