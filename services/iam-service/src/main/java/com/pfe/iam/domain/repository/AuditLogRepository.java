package com.pfe.iam.domain.repository;

import com.pfe.iam.domain.model.AuditLog;

import java.util.List;
import java.util.UUID;

public interface AuditLogRepository {
    AuditLog save(AuditLog auditLog);

    List<AuditLog> findByUserId(UUID userId);

    List<AuditLog> findAll();
}
