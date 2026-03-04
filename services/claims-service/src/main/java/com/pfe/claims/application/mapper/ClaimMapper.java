package com.pfe.claims.application.mapper;

import com.pfe.claims.application.dto.*;
import com.pfe.claims.domain.model.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ClaimMapper {

    ClaimDto toDto(Claim claim);

    ClaimDocumentDto toDto(ClaimDocument document);

    ClaimAssessmentDto toDto(ClaimAssessment assessment);

    PayoutDto toDto(Payout payout);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "claimNumber", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "approvedAmount", ignore = true)
    @Mapping(target = "approvedBy", ignore = true)
    @Mapping(target = "assignedTo", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "documents", ignore = true)
    @Mapping(target = "assessments", ignore = true)
    @Mapping(target = "payout", ignore = true)
    @Mapping(target = "domainEvents", ignore = true)
    Claim toDomain(CreateClaimRequest request);
}
