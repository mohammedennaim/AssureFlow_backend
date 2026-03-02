package com.pfe.client.infrastructure.persistence.repository;

import com.pfe.client.domain.model.ClientHistory;
import com.pfe.client.domain.repository.ClientHistoryRepository;
import com.pfe.client.infrastructure.persistence.mapper.ClientHistoryEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ClientHistoryRepositoryAdapter implements ClientHistoryRepository {

    private final JpaClientHistoryRepository jpaClientHistoryRepository;
    private final ClientHistoryEntityMapper mapper;

    @Override
    public ClientHistory save(ClientHistory history) {
        var entity = mapper.toEntity(history);
        var saved = jpaClientHistoryRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public List<ClientHistory> findByClientId(String clientId) {
        return jpaClientHistoryRepository.findByClientIdOrderByPerformedAtDesc(clientId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<ClientHistory> findAll() {
        return jpaClientHistoryRepository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}
