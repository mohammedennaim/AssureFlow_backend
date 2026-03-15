package com.pfe.client.infrastructure.persistence.mapper;

import com.pfe.client.domain.model.Beneficiary;
import com.pfe.client.infrastructure.persistence.entity.BeneficiaryEntity;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-14T22:53:30+0000",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.45.0.v20260224-0835, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class BeneficiaryEntityMapperImpl implements BeneficiaryEntityMapper {

    @Override
    public BeneficiaryEntity toEntity(Beneficiary beneficiary) {
        if ( beneficiary == null ) {
            return null;
        }

        BeneficiaryEntity.BeneficiaryEntityBuilder beneficiaryEntity = BeneficiaryEntity.builder();

        beneficiaryEntity.clientId( beneficiary.getClientId() );
        beneficiaryEntity.email( beneficiary.getEmail() );
        beneficiaryEntity.firstName( beneficiary.getFirstName() );
        beneficiaryEntity.id( beneficiary.getId() );
        beneficiaryEntity.lastName( beneficiary.getLastName() );
        beneficiaryEntity.percentage( beneficiary.getPercentage() );
        beneficiaryEntity.phone( beneficiary.getPhone() );
        beneficiaryEntity.relationship( beneficiary.getRelationship() );

        return beneficiaryEntity.build();
    }

    @Override
    public Beneficiary toDomain(BeneficiaryEntity entity) {
        if ( entity == null ) {
            return null;
        }

        Beneficiary.BeneficiaryBuilder beneficiary = Beneficiary.builder();

        beneficiary.clientId( entity.getClientId() );
        beneficiary.email( entity.getEmail() );
        beneficiary.firstName( entity.getFirstName() );
        beneficiary.id( entity.getId() );
        beneficiary.lastName( entity.getLastName() );
        beneficiary.percentage( entity.getPercentage() );
        beneficiary.phone( entity.getPhone() );
        beneficiary.relationship( entity.getRelationship() );

        return beneficiary.build();
    }
}
