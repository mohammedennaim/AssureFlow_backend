package com.pfe.iam.infrastructure.persistence.repository;

import com.pfe.iam.infrastructure.persistence.entity.AuditLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaAuditLogRepository extends JpaRepository<AuditLogEntity, String> {
    List<AuditLogEntity> findByUserIdOrderByTimestampDesc(String userId);

    List<AuditLogEntity> findAllByOrderByTimestampDesc();
}
