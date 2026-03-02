package com.pfe.client.application.service;

import com.pfe.client.application.dto.AddressDto;

import java.util.List;

public interface AddressService {
    AddressDto addAddress(String clientId, AddressDto addressDto);

    List<AddressDto> getAddressesByClientId(String clientId);

    AddressDto getAddressById(String id);

    AddressDto updateAddress(String id, AddressDto addressDto);

    void deleteAddress(String id);
}
