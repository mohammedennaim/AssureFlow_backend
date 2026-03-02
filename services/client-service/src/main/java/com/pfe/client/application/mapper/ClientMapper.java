package com.pfe.client.application.mapper;

import com.pfe.client.application.dto.AddressDto;
import com.pfe.client.application.dto.ClientRequest;
import com.pfe.client.application.dto.ClientResponse;
import com.pfe.client.domain.model.Address;
import com.pfe.client.domain.model.Client;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ClientMapper {
    ClientMapper INSTANCE = Mappers.getMapper(ClientMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "clientNumber", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Client toDomain(ClientRequest request);

    ClientResponse toResponse(Client client);

    @Mapping(target = "clientId", ignore = true)
    Address toAddressDomain(AddressDto addressDto);

    AddressDto toAddressDto(Address address);

    List<Address> toAddressDomainList(List<AddressDto> dtos);

    List<AddressDto> toAddressDtoList(List<Address> addresses);
}
