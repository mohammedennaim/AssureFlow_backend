package com.pfe.iam.application.service.impl;

import com.pfe.iam.application.dto.AuditLogDto;
import com.pfe.iam.application.service.AuditService;
import com.pfe.iam.domain.model.AuditLog;
import com.pfe.iam.domain.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditServiceImpl implements AuditService {

    private final AuditLogRepository auditLogRepository;

    @Override
    public void log(String userId, String action) {
        AuditLog auditLog = AuditLog.builder()
                .userId(UUID.fromString(userId))
                .action(action)
                .timestamp(LocalDateTime.now())
                .build();
        auditLogRepository.save(auditLog);
        log.debug("Audit log: user={}, action={}", userId, action);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public List<AuditLogDto> getAuditLogsByUserId(String userId) {
        return auditLogRepository.findByUserId(UUID.fromString(userId)).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public List<AuditLogDto> getAllAuditLogs() {
        return auditLogRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private AuditLogDto toDto(AuditLog auditLog) {
        return AuditLogDto.builder()
                .id(auditLog.getId() != null ? auditLog.getId().toString() : null)
                .userId(auditLog.getUserId() != null ? auditLog.getUserId().toString() : null)
                .action(auditLog.getAction())
                .timestamp(auditLog.getTimestamp())
                .build();
    }
}
