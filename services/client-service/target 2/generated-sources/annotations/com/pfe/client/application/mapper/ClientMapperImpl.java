package com.pfe.client.application.mapper;

import com.pfe.client.application.dto.AddressDto;
import com.pfe.client.application.dto.ClientRequest;
import com.pfe.client.application.dto.ClientResponse;
import com.pfe.client.domain.model.Address;
import com.pfe.client.domain.model.Client;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-14T22:53:30+0000",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.45.0.v20260224-0835, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class ClientMapperImpl implements ClientMapper {

    @Override
    public Client toDomain(ClientRequest request) {
        if ( request == null ) {
            return null;
        }

        Client.ClientBuilder client = Client.builder();

        client.addresses( toAddressDomainList( request.getAddresses() ) );
        client.cin( request.getCin() );
        client.dateOfBirth( request.getDateOfBirth() );
        client.email( request.getEmail() );
        client.firstName( request.getFirstName() );
        client.lastName( request.getLastName() );
        client.phone( request.getPhone() );
        client.type( request.getType() );
        client.userId( request.getUserId() );

        return client.build();
    }

    @Override
    public ClientResponse toResponse(Client client) {
        if ( client == null ) {
            return null;
        }

        ClientResponse.ClientResponseBuilder clientResponse = ClientResponse.builder();

        clientResponse.addresses( toAddressDtoList( client.getAddresses() ) );
        clientResponse.cin( client.getCin() );
        clientResponse.clientNumber( client.getClientNumber() );
        clientResponse.createdAt( client.getCreatedAt() );
        clientResponse.dateOfBirth( client.getDateOfBirth() );
        clientResponse.email( client.getEmail() );
        clientResponse.firstName( client.getFirstName() );
        clientResponse.id( client.getId() );
        clientResponse.lastName( client.getLastName() );
        clientResponse.phone( client.getPhone() );
        clientResponse.status( client.getStatus() );
        clientResponse.type( client.getType() );
        clientResponse.updatedAt( client.getUpdatedAt() );
        clientResponse.userId( client.getUserId() );

        return clientResponse.build();
    }

    @Override
    public Address toAddressDomain(AddressDto addressDto) {
        if ( addressDto == null ) {
            return null;
        }

        Address.AddressBuilder address = Address.builder();

        address.city( addressDto.getCity() );
        address.country( addressDto.getCountry() );
        address.id( addressDto.getId() );
        address.postalCode( addressDto.getPostalCode() );
        address.primary( addressDto.isPrimary() );
        address.street( addressDto.getStreet() );

        return address.build();
    }

    @Override
    public AddressDto toAddressDto(Address address) {
        if ( address == null ) {
            return null;
        }

        AddressDto.AddressDtoBuilder addressDto = AddressDto.builder();

        addressDto.city( address.getCity() );
        addressDto.country( address.getCountry() );
        addressDto.id( address.getId() );
        addressDto.postalCode( address.getPostalCode() );
        addressDto.primary( address.isPrimary() );
        addressDto.street( address.getStreet() );

        return addressDto.build();
    }

    @Override
    public List<Address> toAddressDomainList(List<AddressDto> dtos) {
        if ( dtos == null ) {
            return null;
        }

        List<Address> list = new ArrayList<Address>( dtos.size() );
        for ( AddressDto addressDto : dtos ) {
            list.add( toAddressDomain( addressDto ) );
        }

        return list;
    }

    @Override
    public List<AddressDto> toAddressDtoList(List<Address> addresses) {
        if ( addresses == null ) {
            return null;
        }

        List<AddressDto> list = new ArrayList<AddressDto>( addresses.size() );
        for ( Address address : addresses ) {
            list.add( toAddressDto( address ) );
        }

        return list;
    }
}
