package com.pfe.client.application.mapper;

import com.pfe.client.application.dto.BeneficiaryRequest;
import com.pfe.client.application.dto.BeneficiaryResponse;
import com.pfe.client.domain.model.Beneficiary;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-14T22:53:30+0000",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.45.0.v20260224-0835, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class BeneficiaryMapperImpl implements BeneficiaryMapper {

    @Override
    public Beneficiary toDomain(BeneficiaryRequest request) {
        if ( request == null ) {
            return null;
        }

        Beneficiary.BeneficiaryBuilder beneficiary = Beneficiary.builder();

        beneficiary.email( request.getEmail() );
        beneficiary.firstName( request.getFirstName() );
        beneficiary.lastName( request.getLastName() );
        beneficiary.percentage( request.getPercentage() );
        beneficiary.phone( request.getPhone() );
        beneficiary.relationship( request.getRelationship() );

        return beneficiary.build();
    }

    @Override
    public BeneficiaryResponse toResponse(Beneficiary beneficiary) {
        if ( beneficiary == null ) {
            return null;
        }

        BeneficiaryResponse.BeneficiaryResponseBuilder beneficiaryResponse = BeneficiaryResponse.builder();

        beneficiaryResponse.email( beneficiary.getEmail() );
        beneficiaryResponse.firstName( beneficiary.getFirstName() );
        beneficiaryResponse.id( beneficiary.getId() );
        beneficiaryResponse.lastName( beneficiary.getLastName() );
        beneficiaryResponse.percentage( beneficiary.getPercentage() );
        beneficiaryResponse.phone( beneficiary.getPhone() );
        beneficiaryResponse.relationship( beneficiary.getRelationship() );

        return beneficiaryResponse.build();
    }
}
