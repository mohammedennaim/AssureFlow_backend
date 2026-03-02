package com.pfe.iam.domain.repository;

import com.pfe.iam.domain.model.AuditLog;

import java.util.List;

public interface AuditLogRepository {
    AuditLog save(AuditLog auditLog);

    List<AuditLog> findByUserId(String userId);

    List<AuditLog> findAll();
}
