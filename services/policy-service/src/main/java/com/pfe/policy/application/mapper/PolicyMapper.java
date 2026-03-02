package com.pfe.policy.application.mapper;

import com.pfe.policy.application.dto.*;
import com.pfe.policy.domain.model.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PolicyMapper {

    PolicyDto toDto(Policy policy);

    CoverageDto toDto(Coverage coverage);

    BeneficiaryDto toDto(Beneficiary beneficiary);

    PolicyDocumentDto toDto(PolicyDocument document);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "policyNumber", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "documents", ignore = true)
    Policy toDomain(CreatePolicyRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "policyId", ignore = true)
    Coverage toDomain(CoverageDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "policyId", ignore = true)
    Beneficiary toDomain(BeneficiaryDto dto);
}
