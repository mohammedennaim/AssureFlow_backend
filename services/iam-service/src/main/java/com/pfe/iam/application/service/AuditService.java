package com.pfe.iam.application.service;

import com.pfe.iam.application.dto.AuditLogDto;

import java.util.List;

public interface AuditService {
    void log(String userId, String action);

    List<AuditLogDto> getAuditLogsByUserId(String userId);

    List<AuditLogDto> getAllAuditLogs();
}
