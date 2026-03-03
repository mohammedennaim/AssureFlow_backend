package com.pfe.claims.infrastructure.persistence.mapper;

import com.pfe.claims.domain.model.Claim;
import com.pfe.claims.domain.model.ClaimAssessment;
import com.pfe.claims.domain.model.ClaimDocument;
import com.pfe.claims.domain.model.ClaimPayout;
import com.pfe.claims.infrastructure.persistence.entity.ClaimAssessmentEntity;
import com.pfe.claims.infrastructure.persistence.entity.ClaimDocumentEntity;
import com.pfe.claims.infrastructure.persistence.entity.ClaimEntity;
import com.pfe.claims.infrastructure.persistence.entity.ClaimPayoutEntity;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ClaimEntityMapper {

    Claim toDomain(ClaimEntity entity);

    @Mapping(target = "claimId", source = "claim.id")
    ClaimDocument toDomain(ClaimDocumentEntity entity);

    @Mapping(target = "claimId", source = "claim.id")
    ClaimAssessment toDomain(ClaimAssessmentEntity entity);

    @Mapping(target = "claimId", source = "claim.id")
    ClaimPayout toDomain(ClaimPayoutEntity entity);

    @Mapping(target = "updatedAt", ignore = true)
    ClaimEntity toEntity(Claim domain);

    @Mapping(target = "claim", ignore = true)
    ClaimDocumentEntity toEntity(ClaimDocument domain);

    @Mapping(target = "claim", ignore = true)
    ClaimAssessmentEntity toEntity(ClaimAssessment domain);

    @Mapping(target = "claim", ignore = true)
    ClaimPayoutEntity toEntity(ClaimPayout domain);

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
