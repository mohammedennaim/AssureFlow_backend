package com.pfe.workflow.application.service;

import com.pfe.workflow.application.dto.CreateSLADefinitionRequest;
import com.pfe.workflow.application.dto.SLADefinitionDto;
import com.pfe.workflow.application.dto.SLAViolationDto;
import com.pfe.workflow.domain.model.SLAStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface SLAService {
    
    SLADefinitionDto createSLADefinition(CreateSLADefinitionRequest request);
    
    SLADefinitionDto getSLADefinitionById(UUID id);
    
    List<SLADefinitionDto> getAllSLADefinitions();
    
    List<SLADefinitionDto> getActiveSLADefinitions();
    
    SLADefinitionDto getSLADefinitionByEntityType(String entityType);
    
    void trackSLA(UUID entityId, String entityType, LocalDateTime createdAt);
    
    void checkSLAViolations();
    
    Page<SLAViolationDto> getAllViolations(Pageable pageable);
    
    Page<SLAViolationDto> getViolationsByEntity(UUID entityId, Pageable pageable);
    
    Page<SLAViolationDto> getViolationsByStatus(SLAStatus status, Pageable pageable);
    
    void resolveSLAViolation(UUID violationId);
}
