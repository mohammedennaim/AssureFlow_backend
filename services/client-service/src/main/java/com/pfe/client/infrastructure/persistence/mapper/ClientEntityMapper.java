package com.pfe.client.infrastructure.persistence.mapper;

import com.pfe.client.domain.model.Address;
import com.pfe.client.domain.model.Client;
import com.pfe.client.infrastructure.persistence.entity.AddressEntity;
import com.pfe.client.infrastructure.persistence.entity.ClientEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ClientEntityMapper {
    ClientEntityMapper INSTANCE = Mappers.getMapper(ClientEntityMapper.class);

    @Mapping(target = "addresses", ignore = true)
    ClientEntity toEntity(Client client);

    @Mapping(target = "addresses", ignore = true)
    Client toDomain(ClientEntity entity);

    AddressEntity toAddressEntity(Address address);

    Address toAddressDomain(AddressEntity entity);

    List<Address> toAddressDomainList(List<AddressEntity> entities);

    List<AddressEntity> toAddressEntityList(List<Address> addresses);
}
