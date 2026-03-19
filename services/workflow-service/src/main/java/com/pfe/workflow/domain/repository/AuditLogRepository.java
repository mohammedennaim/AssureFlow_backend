package com.pfe.workflow.domain.repository;

import com.pfe.workflow.domain.model.AuditAction;
import com.pfe.workflow.domain.model.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {
    
    Page<AuditLog> findByEntityTypeAndEntityId(String entityType, UUID entityId, Pageable pageable);
    
    Page<AuditLog> findByUserId(UUID userId, Pageable pageable);
    
    Page<AuditLog> findByAction(AuditAction action, Pageable pageable);
    
    Page<AuditLog> findByEntityType(String entityType, Pageable pageable);
    
    List<AuditLog> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
    
    Page<AuditLog> findAllByOrderByTimestampDesc(Pageable pageable);
}
