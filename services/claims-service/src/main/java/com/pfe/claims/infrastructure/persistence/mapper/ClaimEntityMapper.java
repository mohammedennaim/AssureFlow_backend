package com.pfe.claims.infrastructure.persistence.mapper;

import com.pfe.claims.domain.model.Claim;
import com.pfe.claims.domain.model.ClaimAssessment;
import com.pfe.claims.domain.model.ClaimDocument;
import com.pfe.claims.domain.model.Payout;
import com.pfe.claims.infrastructure.persistence.entity.ClaimAssessmentEntity;
import com.pfe.claims.infrastructure.persistence.entity.ClaimDocumentEntity;
import com.pfe.claims.infrastructure.persistence.entity.ClaimEntity;
import com.pfe.claims.infrastructure.persistence.entity.PayoutEntity;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ClaimEntityMapper {

    @Mapping(target = "slaDeadline", ignore = true)
    @Mapping(target = "resolvedAt", ignore = true)
    @Mapping(target = "domainEvents", ignore = true)
    Claim toDomain(ClaimEntity entity);

    @Mapping(target = "claimId", source = "claim.id")
    ClaimDocument toDomain(ClaimDocumentEntity entity);

    @Mapping(target = "claimId", source = "claim.id")
    ClaimAssessment toDomain(ClaimAssessmentEntity entity);

    @Mapping(target = "claimId", source = "claim.id")
    Payout toDomain(PayoutEntity entity);

    @Mapping(target = "updatedAt", ignore = true)
    ClaimEntity toEntity(Claim domain);

    @Mapping(target = "claim", ignore = true)
    ClaimDocumentEntity toEntity(ClaimDocument domain);

    @Mapping(target = "claim", ignore = true)
    ClaimAssessmentEntity toEntity(ClaimAssessment domain);

    @Mapping(target = "claim", ignore = true)
    PayoutEntity toEntity(Payout domain);

    @AfterMapping
    default void linkClaimToChildren(@MappingTarget ClaimEntity claimEntity) {
        if (claimEntity.getDocuments() != null) {
            claimEntity.getDocuments().forEach(d -> d.setClaim(claimEntity));
        }
        if (claimEntity.getAssessments() != null) {
            claimEntity.getAssessments().forEach(a -> a.setClaim(claimEntity));
        }
        if (claimEntity.getPayout() != null) {
            claimEntity.getPayout().setClaim(claimEntity);
        }
    }
}
