package com.pfe.client.application.service;

import com.pfe.client.application.dto.AddressDto;

import java.util.List;
import java.util.UUID;

public interface AddressService {
    AddressDto addAddress(UUID clientId, AddressDto addressDto);

    List<AddressDto> getAddressesByClientId(UUID clientId);

    AddressDto getAddressById(UUID id);

    AddressDto updateAddress(UUID id, AddressDto addressDto);

    void deleteAddress(UUID id);
}
