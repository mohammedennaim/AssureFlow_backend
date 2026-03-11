package com.pfe.client.application.service.impl;

import com.pfe.client.application.dto.AddressDto;
import com.pfe.client.application.mapper.ClientMapper;
import com.pfe.client.application.service.AddressService;
import com.pfe.client.domain.exception.ClientNotFoundException;
import com.pfe.client.domain.model.Address;
import com.pfe.client.domain.repository.AddressRepository;
import com.pfe.client.domain.repository.ClientRepository;
import com.pfe.commons.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final ClientRepository clientRepository;
    private final ClientMapper mapper;

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    @Transactional
    public AddressDto addAddress(UUID clientId, AddressDto addressDto) {
        clientRepository.findById(clientId)
                .orElseThrow(() -> new ClientNotFoundException(clientId));

        Address address = mapper.toAddressDomain(addressDto);
        address.setClientId(clientId);
        Address saved = addressRepository.save(address);

        log.info("Added address for client {}", clientId);
        return mapper.toAddressDto(saved);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT', 'CLIENT')")
    @Transactional(readOnly = true)
    public List<AddressDto> getAddressesByClientId(UUID clientId) {
        return addressRepository.findByClientId(clientId).stream()
                .map(mapper::toAddressDto)
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT', 'CLIENT')")
    @Transactional(readOnly = true)
    public AddressDto getAddressById(UUID id) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", id));
        return mapper.toAddressDto(address);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    @Transactional
    public AddressDto updateAddress(UUID id, AddressDto addressDto) {
        Address existing = addressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", id));

        existing.setStreet(addressDto.getStreet());
        existing.setCity(addressDto.getCity());
        existing.setPostalCode(addressDto.getPostalCode());
        existing.setCountry(addressDto.getCountry());
        existing.setPrimary(addressDto.isPrimary());

        Address saved = addressRepository.save(existing);
        log.info("Updated address {}", id);
        return mapper.toAddressDto(saved);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void deleteAddress(UUID id) {
        addressRepository.deleteById(id);
        log.info("Deleted address {}", id);
    }
}
