package com.pfe.policy.infrastructure.persistence.mapper;

import com.pfe.policy.domain.model.Beneficiary;
import com.pfe.policy.domain.model.Coverage;
import com.pfe.policy.domain.model.Policy;
import com.pfe.policy.domain.model.PolicyDocument;
import com.pfe.policy.infrastructure.persistence.entity.BeneficiaryEntity;
import com.pfe.policy.infrastructure.persistence.entity.CoverageEntity;
import com.pfe.policy.infrastructure.persistence.entity.PolicyDocumentEntity;
import com.pfe.policy.infrastructure.persistence.entity.PolicyEntity;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PolicyEntityMapper {

    Policy toDomain(PolicyEntity entity);

    @Mapping(target = "policyId", source = "policy.id")
    Coverage toDomain(CoverageEntity entity);

    @Mapping(target = "policyId", source = "policy.id")
    Beneficiary toDomain(BeneficiaryEntity entity);

    @Mapping(target = "policyId", source = "policy.id")
    PolicyDocument toDomain(PolicyDocumentEntity entity);

    PolicyEntity toEntity(Policy domain);

    @Mapping(target = "policy", ignore = true)
    CoverageEntity toEntity(Coverage domain);

    @Mapping(target = "policy", ignore = true)
    BeneficiaryEntity toEntity(Beneficiary domain);

    @Mapping(target = "policy", ignore = true)
    PolicyDocumentEntity toEntity(PolicyDocument domain);

    @AfterMapping
    default void linkPolicyToChildren(@MappingTarget PolicyEntity policyEntity) {
        if (policyEntity.getCoverages() != null) {
            policyEntity.getCoverages().forEach(c -> c.setPolicy(policyEntity));
        }
        if (policyEntity.getBeneficiaries() != null) {
            policyEntity.getBeneficiaries().forEach(b -> b.setPolicy(policyEntity));
        }
        if (policyEntity.getDocuments() != null) {
            policyEntity.getDocuments().forEach(d -> d.setPolicy(policyEntity));
        }
    }
}
