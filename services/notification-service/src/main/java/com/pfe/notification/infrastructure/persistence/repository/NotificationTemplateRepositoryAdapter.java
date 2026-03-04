package com.pfe.notification.infrastructure.persistence.repository;

import com.pfe.notification.domain.model.NotificationTemplate;
import com.pfe.notification.domain.repository.NotificationTemplateRepository;
import com.pfe.notification.infrastructure.persistence.mapper.NotificationTemplateEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class NotificationTemplateRepositoryAdapter implements NotificationTemplateRepository {

    private final JpaNotificationTemplateRepository jpaRepository;
    private final NotificationTemplateEntityMapper mapper;

    @Override
    public NotificationTemplate save(NotificationTemplate template) {
        var entity = mapper.toEntity(template);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<NotificationTemplate> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<NotificationTemplate> findByName(String name) {
        return jpaRepository.findByName(name).map(mapper::toDomain);
    }

    @Override
    public List<NotificationTemplate> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }
}
