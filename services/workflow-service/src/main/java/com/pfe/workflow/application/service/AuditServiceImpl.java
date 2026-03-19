package com.pfe.workflow.application.service;

import com.pfe.workflow.application.dto.AuditLogDto;
import com.pfe.workflow.application.dto.CreateAuditRequest;
import com.pfe.workflow.domain.model.AuditAction;
import com.pfe.workflow.domain.model.AuditLog;
import com.pfe.workflow.domain.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuditServiceImpl implements AuditService {

    private final AuditLogRepository auditLogRepository;

    @Override
    public AuditLogDto createAuditLog(CreateAuditRequest request) {
        log.info("Creating audit log for action: {} on entity: {}/{}", 
                request.getAction(), request.getEntityType(), request.getEntityId());
        
        AuditLog auditLog = AuditLog.builder()
                .userId(request.getUserId())
                .username(request.getUsername())
                .action(request.getAction())
                .entityType(request.getEntityType())
                .entityId(request.getEntityId())
                .oldValue(request.getOldValue())
                .newValue(request.getNewValue())
                .reason(request.getReason())
                .ipAddress(request.getIpAddress())
                .userAgent(request.getUserAgent())
                .timestamp(LocalDateTime.now())
                .build();
        
        AuditLog saved = auditLogRepository.save(auditLog);
        return mapToDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditLogDto> getAllAuditLogs(Pageable pageable) {
        return auditLogRepository.findAllByOrderByTimestampDesc(pageable)
                .map(this::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public AuditLogDto getAuditLogById(UUID id) {
        return auditLogRepository.findById(id)
                .map(this::mapToDto)
                .orElseThrow(() -> new RuntimeException("Audit log not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditLogDto> getAuditLogsByEntity(String entityType, UUID entityId, Pageable pageable) {
        return auditLogRepository.findByEntityTypeAndEntityId(entityType, entityId, pageable)
                .map(this::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditLogDto> getAuditLogsByUser(UUID userId, Pageable pageable) {
        return auditLogRepository.findByUserId(userId, pageable)
                .map(this::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditLogDto> getAuditLogsByAction(AuditAction action, Pageable pageable) {
        return auditLogRepository.findByAction(action, pageable)
                .map(this::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditLogDto> getAuditLogsByEntityType(String entityType, Pageable pageable) {
        return auditLogRepository.findByEntityType(entityType, pageable)
                .map(this::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditLogDto> getAuditLogsByDateRange(LocalDateTime start, LocalDateTime end) {
        return auditLogRepository.findByTimestampBetween(start, end).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private AuditLogDto mapToDto(AuditLog auditLog) {
        return AuditLogDto.builder()
                .id(auditLog.getId())
                .userId(auditLog.getUserId())
                .username(auditLog.getUsername())
                .action(auditLog.getAction())
                .entityType(auditLog.getEntityType())
                .entityId(auditLog.getEntityId())
                .oldValue(auditLog.getOldValue())
                .newValue(auditLog.getNewValue())
                .reason(auditLog.getReason())
                .timestamp(auditLog.getTimestamp())
                .ipAddress(auditLog.getIpAddress())
                .userAgent(auditLog.getUserAgent())
                .build();
    }
}
