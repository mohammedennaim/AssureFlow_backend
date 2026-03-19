package com.pfe.workflow.application.service;

import com.pfe.workflow.application.dto.CreateEscalationRequest;
import com.pfe.workflow.application.dto.EscalationDto;
import com.pfe.workflow.application.dto.ResolveEscalationRequest;
import com.pfe.workflow.domain.model.EscalationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface EscalationService {
    
    EscalationDto createEscalation(CreateEscalationRequest request);
    
    UUID createAutoEscalation(UUID entityId, String entityType, UUID slaViolationId);
    
    EscalationDto getEscalationById(UUID id);
    
    Page<EscalationDto> getAllEscalations(Pageable pageable);
    
    Page<EscalationDto> getEscalationsByStatus(EscalationStatus status, Pageable pageable);
    
    Page<EscalationDto> getEscalationsByAssignedTo(UUID assignedTo, Pageable pageable);
    
    EscalationDto resolveEscalation(UUID id, ResolveEscalationRequest request);
    
    EscalationDto cancelEscalation(UUID id);
}
