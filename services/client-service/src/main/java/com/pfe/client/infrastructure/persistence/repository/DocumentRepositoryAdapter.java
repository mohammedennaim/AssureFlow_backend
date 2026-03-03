package com.pfe.client.infrastructure.persistence.repository;

import com.pfe.client.domain.model.Document;
import com.pfe.client.domain.repository.DocumentRepository;
import com.pfe.client.infrastructure.persistence.mapper.DocumentEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class DocumentRepositoryAdapter implements DocumentRepository {

    private final JpaDocumentRepository jpaDocumentRepository;
    private final DocumentEntityMapper mapper;

    @Override
    public Document save(Document document) {
        var entity = mapper.toEntity(document);
        var saved = jpaDocumentRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Document> findById(UUID id) {
        return jpaDocumentRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Document> findByClientId(UUID clientId) {
        return jpaDocumentRepository.findByClientId(clientId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        jpaDocumentRepository.deleteById(id);
    }
}
