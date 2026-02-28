package com.pfe.client.application.mapper;

import com.pfe.client.application.dto.AddressDto;
import com.pfe.client.application.dto.ClientRequest;
import com.pfe.client.application.dto.ClientResponse;
import com.pfe.client.domain.model.Address;
import com.pfe.client.domain.model.Client;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ClientMapper {
    ClientMapper INSTANCE = Mappers.getMapper(ClientMapper.class);

    Client toDomain(ClientRequest request);

    ClientResponse toResponse(Client client);

    Address toAddressDomain(AddressDto addressDto);

    AddressDto toAddressDto(Address address);
}
