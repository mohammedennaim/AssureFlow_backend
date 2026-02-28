package com.pfe.client.infrastructure.persistence.mapper;

import com.pfe.client.domain.model.Address;
import com.pfe.client.domain.model.Client;
import com.pfe.client.infrastructure.persistence.entity.AddressEmbeddable;
import com.pfe.client.infrastructure.persistence.entity.ClientEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ClientEntityMapper {
    ClientEntityMapper INSTANCE = Mappers.getMapper(ClientEntityMapper.class);

    ClientEntity toEntity(Client client);

    Client toDomain(ClientEntity entity);

    AddressEmbeddable toAddressEmbeddable(Address address);

    Address toAddressDomain(AddressEmbeddable addressEmbeddable);
}
