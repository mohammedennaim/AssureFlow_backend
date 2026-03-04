package com.pfe.iam.infrastructure.persistence.repository;

import com.pfe.iam.domain.model.AuditLog;
import com.pfe.iam.domain.repository.AuditLogRepository;
import com.pfe.iam.infrastructure.persistence.entity.AuditLogEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AuditLogRepositoryAdapter implements AuditLogRepository {

    private final JpaAuditLogRepository jpaAuditLogRepository;

    @Override
    public AuditLog save(AuditLog auditLog) {
        AuditLogEntity entity = toEntity(auditLog);
        AuditLogEntity saved = jpaAuditLogRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public List<AuditLog> findByUserId(UUID userId) {
        return jpaAuditLogRepository.findByUserIdOrderByTimestampDesc(userId.toString()).stream()
                .map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<AuditLog> findAll() {
        return jpaAuditLogRepository.findAllByOrderByTimestampDesc().stream()
                .map(this::toDomain).collect(Collectors.toList());
    }

    private AuditLog toDomain(AuditLogEntity entity) {
        return AuditLog.builder()
                .id(UUID.fromString(entity.getId()))
                .userId(UUID.fromString(entity.getUserId()))
                .action(entity.getAction())
                .timestamp(entity.getTimestamp())
                .build();
    }

    private AuditLogEntity toEntity(AuditLog auditLog) {
        return AuditLogEntity.builder()
                .id(auditLog.getId() != null ? auditLog.getId().toString() : null)
                .userId(auditLog.getUserId() != null ? auditLog.getUserId().toString() : null)
                .action(auditLog.getAction())
                .timestamp(auditLog.getTimestamp())
                .build();
    }
}
