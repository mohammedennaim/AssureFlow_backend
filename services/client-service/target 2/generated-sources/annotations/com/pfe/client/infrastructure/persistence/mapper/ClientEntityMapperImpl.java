package com.pfe.client.infrastructure.persistence.mapper;

import com.pfe.client.domain.model.Address;
import com.pfe.client.domain.model.Client;
import com.pfe.client.infrastructure.persistence.entity.AddressEntity;
import com.pfe.client.infrastructure.persistence.entity.ClientEntity;
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
public class ClientEntityMapperImpl implements ClientEntityMapper {

    @Override
    public ClientEntity toEntity(Client client) {
        if ( client == null ) {
            return null;
        }

        ClientEntity.ClientEntityBuilder clientEntity = ClientEntity.builder();

        clientEntity.active( client.isActive() );
        clientEntity.cin( client.getCin() );
        clientEntity.clientNumber( client.getClientNumber() );
        clientEntity.createdAt( client.getCreatedAt() );
        clientEntity.dateOfBirth( client.getDateOfBirth() );
        clientEntity.email( client.getEmail() );
        clientEntity.firstName( client.getFirstName() );
        clientEntity.id( client.getId() );
        clientEntity.lastName( client.getLastName() );
        clientEntity.phone( client.getPhone() );
        clientEntity.status( client.getStatus() );
        clientEntity.type( client.getType() );
        clientEntity.updatedAt( client.getUpdatedAt() );
        clientEntity.userId( client.getUserId() );

        return clientEntity.build();
    }

    @Override
    public Client toDomain(ClientEntity entity) {
        if ( entity == null ) {
            return null;
        }

        Client.ClientBuilder client = Client.builder();

        client.active( entity.isActive() );
        client.cin( entity.getCin() );
        client.clientNumber( entity.getClientNumber() );
        client.createdAt( entity.getCreatedAt() );
        client.dateOfBirth( entity.getDateOfBirth() );
        client.email( entity.getEmail() );
        client.firstName( entity.getFirstName() );
        client.id( entity.getId() );
        client.lastName( entity.getLastName() );
        client.phone( entity.getPhone() );
        client.status( entity.getStatus() );
        client.type( entity.getType() );
        client.updatedAt( entity.getUpdatedAt() );
        client.userId( entity.getUserId() );

        return client.build();
    }

    @Override
    public AddressEntity toAddressEntity(Address address) {
        if ( address == null ) {
            return null;
        }

        AddressEntity.AddressEntityBuilder addressEntity = AddressEntity.builder();

        addressEntity.city( address.getCity() );
        addressEntity.clientId( address.getClientId() );
        addressEntity.country( address.getCountry() );
        addressEntity.id( address.getId() );
        addressEntity.postalCode( address.getPostalCode() );
        addressEntity.primary( address.isPrimary() );
        addressEntity.street( address.getStreet() );

        return addressEntity.build();
    }

    @Override
    public Address toAddressDomain(AddressEntity entity) {
        if ( entity == null ) {
            return null;
        }

        Address.AddressBuilder address = Address.builder();

        address.city( entity.getCity() );
        address.clientId( entity.getClientId() );
        address.country( entity.getCountry() );
        address.id( entity.getId() );
        address.postalCode( entity.getPostalCode() );
        address.primary( entity.isPrimary() );
        address.street( entity.getStreet() );

        return address.build();
    }

    @Override
    public List<Address> toAddressDomainList(List<AddressEntity> entities) {
        if ( entities == null ) {
            return null;
        }

        List<Address> list = new ArrayList<Address>( entities.size() );
        for ( AddressEntity addressEntity : entities ) {
            list.add( toAddressDomain( addressEntity ) );
        }

        return list;
    }

    @Override
    public List<AddressEntity> toAddressEntityList(List<Address> addresses) {
        if ( addresses == null ) {
            return null;
        }

        List<AddressEntity> list = new ArrayList<AddressEntity>( addresses.size() );
        for ( Address address : addresses ) {
            list.add( toAddressEntity( address ) );
        }

        return list;
    }
}
