package com.pfe.workflow.application.service;

import com.pfe.workflow.application.dto.AuditLogDto;
import com.pfe.workflow.application.dto.CreateAuditRequest;
import com.pfe.workflow.domain.model.AuditAction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface AuditService {
    
    AuditLogDto createAuditLog(CreateAuditRequest request);
    
    Page<AuditLogDto> getAllAuditLogs(Pageable pageable);
    
    AuditLogDto getAuditLogById(UUID id);
    
    Page<AuditLogDto> getAuditLogsByEntity(String entityType, UUID entityId, Pageable pageable);
    
    Page<AuditLogDto> getAuditLogsByUser(UUID userId, Pageable pageable);
    
    Page<AuditLogDto> getAuditLogsByAction(AuditAction action, Pageable pageable);
    
    Page<AuditLogDto> getAuditLogsByEntityType(String entityType, Pageable pageable);
    
    List<AuditLogDto> getAuditLogsByDateRange(LocalDateTime start, LocalDateTime end);
}
