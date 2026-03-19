package com.pfe.workflow.application.service;

import com.pfe.workflow.application.dto.CreateEscalationRequest;
import com.pfe.workflow.application.dto.EscalationDto;
import com.pfe.workflow.application.dto.ResolveEscalationRequest;
import com.pfe.workflow.domain.model.Escalation;
import com.pfe.workflow.domain.model.EscalationLevel;
import com.pfe.workflow.domain.model.EscalationStatus;
import com.pfe.workflow.domain.repository.EscalationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class EscalationServiceImpl implements EscalationService {

    private final EscalationRepository escalationRepository;

    @Override
    public EscalationDto createEscalation(CreateEscalationRequest request) {
        log.info("Creating escalation for entity: {}/{}", request.getEntityType(), request.getEntityId());
        
        Escalation escalation = Escalation.builder()
                .entityId(request.getEntityId())
                .entityType(request.getEntityType())
                .level(request.getLevel())
                .reason(request.getReason())
                .description(request.getDescription())
                .assignedTo(request.getAssignedTo())
                .assignedToName(request.getAssignedToName())
                .slaViolationId(request.getSlaViolationId())
                .status(EscalationStatus.OPEN)
                .build();
        
        Escalation saved = escalationRepository.save(escalation);
        return mapToDto(saved);
    }

    @Override
    public UUID createAutoEscalation(UUID entityId, String entityType, UUID slaViolationId) {
        log.info("Creating auto-escalation for SLA violation: {}", slaViolationId);
        
        Escalation escalation = Escalation.builder()
                .entityId(entityId)
                .entityType(entityType)
                .level(EscalationLevel.LEVEL_1)
                .reason("SLA Violation - Automatic Escalation")
                .description("This escalation was automatically created due to SLA violation")
                .slaViolationId(slaViolationId)
                .status(EscalationStatus.OPEN)
                .build();
        
        Escalation saved = escalationRepository.save(escalation);
        return saved.getId();
    }

    @Override
    @Transactional(readOnly = true)
    public EscalationDto getEscalationById(UUID id) {
        return escalationRepository.findById(id)
                .map(this::mapToDto)
                .orElseThrow(() -> new RuntimeException("Escalation not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EscalationDto> getAllEscalations(Pageable pageable) {
        return escalationRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(this::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EscalationDto> getEscalationsByStatus(EscalationStatus status, Pageable pageable) {
        return escalationRepository.findByStatus(status, pageable)
                .map(this::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EscalationDto> getEscalationsByAssignedTo(UUID assignedTo, Pageable pageable) {
        return escalationRepository.findByAssignedTo(assignedTo, pageable)
                .map(this::mapToDto);
    }

    @Override
    public EscalationDto resolveEscalation(UUID id, ResolveEscalationRequest request) {
        log.info("Resolving escalation: {}", id);
        
        Escalation escalation = escalationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Escalation not found with id: " + id));
        
        escalation.setStatus(EscalationStatus.RESOLVED);
        escalation.setResolvedAt(LocalDateTime.now());
        escalation.setResolvedBy(request.getResolvedBy());
        escalation.setResolution(request.getResolution());
        
        Escalation saved = escalationRepository.save(escalation);
        return mapToDto(saved);
    }

    @Override
    public EscalationDto cancelEscalation(UUID id) {
        log.info("Cancelling escalation: {}", id);
        
        Escalation escalation = escalationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Escalation not found with id: " + id));
        
        escalation.setStatus(EscalationStatus.CANCELLED);
        
        Escalation saved = escalationRepository.save(escalation);
        return mapToDto(saved);
    }

    private EscalationDto mapToDto(Escalation escalation) {
        return EscalationDto.builder()
                .id(escalation.getId())
                .entityId(escalation.getEntityId())
                .entityType(escalation.getEntityType())
                .level(escalation.getLevel())
                .status(escalation.getStatus())
                .reason(escalation.getReason())
                .description(escalation.getDescription())
                .assignedTo(escalation.getAssignedTo())
                .assignedToName(escalation.getAssignedToName())
                .slaViolationId(escalation.getSlaViolationId())
                .createdAt(escalation.getCreatedAt())
                .resolvedAt(escalation.getResolvedAt())
                .resolvedBy(escalation.getResolvedBy())
                .resolution(escalation.getResolution())
                .build();
    }
}
