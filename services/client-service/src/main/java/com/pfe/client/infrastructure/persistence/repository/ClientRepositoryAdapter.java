package com.pfe.client.infrastructure.persistence.repository;

import com.pfe.client.domain.model.Client;
import com.pfe.client.domain.repository.ClientRepository;
import com.pfe.client.infrastructure.persistence.entity.ClientEntity;
import com.pfe.client.infrastructure.persistence.mapper.ClientEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ClientRepositoryAdapter implements ClientRepository {

    private final JpaClientRepository jpaClientRepository;
    private final ClientEntityMapper mapper;

    @Override
    public Client save(Client client) {
        ClientEntity entity = mapper.toEntity(client);
        ClientEntity savedEntity = jpaClientRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Client> findById(String id) {
        return jpaClientRepository.findById(id)
                .filter(ClientEntity::isActive)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Client> findByEmail(String email) {
        return jpaClientRepository.findByEmail(email).map(mapper::toDomain);
    }

    @Override
    public Optional<Client> findByCin(String cin) {
        return jpaClientRepository.findByCin(cin).map(mapper::toDomain);
    }

    @Override
    public List<Client> findAll() {
        return jpaClientRepository.findAll().stream()
            .filter(ClientEntity::isActive)
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }

        @Override
        public List<Client> findAll(int page, int size) {
        var p = org.springframework.data.domain.PageRequest.of(page, size);
            return jpaClientRepository.findAll(p).stream()
                .filter(com.pfe.client.infrastructure.persistence.entity.ClientEntity::isActive)
                .map(mapper::toDomain)
                .collect(Collectors.toList());
        }

    @Override
    public void deleteById(String id) {
        jpaClientRepository.findById(id).ifPresent(entity -> {
            entity.setActive(false);
            jpaClientRepository.save(entity);
        });
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaClientRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByCin(String cin) {
        return jpaClientRepository.existsByCin(cin);
    }
}
