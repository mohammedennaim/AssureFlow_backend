package com.pfe.iam.infrastructure.persistence.repository;

import com.pfe.iam.domain.model.AuditLog;
import com.pfe.iam.domain.repository.AuditLogRepository;
import com.pfe.iam.infrastructure.persistence.entity.AuditLogEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
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
    public List<AuditLog> findByUserId(String userId) {
        return jpaAuditLogRepository.findByUserIdOrderByTimestampDesc(userId).stream()
                .map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<AuditLog> findAll() {
        return jpaAuditLogRepository.findAllByOrderByTimestampDesc().stream()
                .map(this::toDomain).collect(Collectors.toList());
    }

    private AuditLog toDomain(AuditLogEntity entity) {
        return AuditLog.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .action(entity.getAction())
                .timestamp(entity.getTimestamp())
                .build();
    }

    private AuditLogEntity toEntity(AuditLog auditLog) {
        return AuditLogEntity.builder()
                .id(auditLog.getId())
                .userId(auditLog.getUserId())
                .action(auditLog.getAction())
                .timestamp(auditLog.getTimestamp())
                .build();
    }
}
