package com.pfe.client.infrastructure.persistence.repository;

import com.pfe.client.domain.model.Address;
import com.pfe.client.domain.repository.AddressRepository;
import com.pfe.client.infrastructure.persistence.entity.AddressEntity;
import com.pfe.client.infrastructure.persistence.mapper.ClientEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class AddressRepositoryAdapter implements AddressRepository {

    private final JpaAddressRepository jpaAddressRepository;
    private final ClientEntityMapper mapper;

    @Override
    public Address save(Address address) {
        AddressEntity entity = mapper.toAddressEntity(address);
        AddressEntity saved = jpaAddressRepository.save(entity);
        return mapper.toAddressDomain(saved);
    }

    @Override
    public Optional<Address> findById(UUID id) {
        return jpaAddressRepository.findById(id).map(mapper::toAddressDomain);
    }

    @Override
    public List<Address> findByClientId(UUID clientId) {
        return jpaAddressRepository.findByClientId(clientId).stream()
                .map(mapper::toAddressDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Address> findByClientIdIn(List<UUID> clientIds) {
        return jpaAddressRepository.findByClientIdIn(clientIds).stream()
                .map(mapper::toAddressDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        jpaAddressRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteByClientId(UUID clientId) {
        jpaAddressRepository.deleteByClientId(clientId);
    }
}
