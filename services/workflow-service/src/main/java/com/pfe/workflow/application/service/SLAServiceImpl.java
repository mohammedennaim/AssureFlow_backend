package com.pfe.workflow.application.service;

import com.pfe.workflow.application.dto.CreateSLADefinitionRequest;
import com.pfe.workflow.application.dto.SLADefinitionDto;
import com.pfe.workflow.application.dto.SLAViolationDto;
import com.pfe.workflow.domain.model.SLADefinition;
import com.pfe.workflow.domain.model.SLAStatus;
import com.pfe.workflow.domain.model.SLAViolation;
import com.pfe.workflow.domain.repository.SLADefinitionRepository;
import com.pfe.workflow.domain.repository.SLAViolationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SLAServiceImpl implements SLAService {

    private final SLADefinitionRepository slaDefinitionRepository;
    private final SLAViolationRepository slaViolationRepository;
    private final EscalationService escalationService;

    @Override
    public SLADefinitionDto createSLADefinition(CreateSLADefinitionRequest request) {
        log.info("Creating SLA definition: {} for entity type: {}", request.getName(), request.getEntityType());
        
        SLADefinition slaDefinition = SLADefinition.builder()
                .name(request.getName())
                .entityType(request.getEntityType())
                .description(request.getDescription())
                .durationHours(request.getDurationHours())
                .autoEscalate(request.getAutoEscalate())
                .active(request.getActive())
                .build();
        
        SLADefinition saved = slaDefinitionRepository.save(slaDefinition);
        return mapToDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public SLADefinitionDto getSLADefinitionById(UUID id) {
        return slaDefinitionRepository.findById(id)
                .map(this::mapToDto)
                .orElseThrow(() -> new RuntimeException("SLA Definition not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SLADefinitionDto> getAllSLADefinitions() {
        return slaDefinitionRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SLADefinitionDto> getActiveSLADefinitions() {
        return slaDefinitionRepository.findByActive(true).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public SLADefinitionDto getSLADefinitionByEntityType(String entityType) {
        return slaDefinitionRepository.findByEntityTypeAndActive(entityType, true)
                .map(this::mapToDto)
                .orElseThrow(() -> new RuntimeException("No active SLA definition found for entity type: " + entityType));
    }

    @Override
    public void trackSLA(UUID entityId, String entityType, LocalDateTime createdAt) {
        log.info("Tracking SLA for entity: {}/{}", entityType, entityId);
        
        // This method is called when an entity is created
        // The actual SLA checking is done by the scheduled job
    }

    @Override
    public void checkSLAViolations() {
        log.info("Checking for SLA violations...");
        
        List<SLADefinition> activeDefinitions = slaDefinitionRepository.findByActive(true);
        
        for (SLADefinition definition : activeDefinitions) {
            // This would need integration with each service to check their entities
            // For now, this is a placeholder that will be called by a scheduled job
            log.debug("Checking SLA: {} for entity type: {}", definition.getName(), definition.getEntityType());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SLAViolationDto> getAllViolations(Pageable pageable) {
        return slaViolationRepository.findAll(pageable)
                .map(this::mapViolationToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SLAViolationDto> getViolationsByEntity(UUID entityId, Pageable pageable) {
        return slaViolationRepository.findByEntityTypeAndEntityId("CLAIM", entityId, pageable)
                .map(this::mapViolationToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SLAViolationDto> getViolationsByStatus(SLAStatus status, Pageable pageable) {
        return slaViolationRepository.findByStatus(status, pageable)
                .map(this::mapViolationToDto);
    }

    @Override
    public void resolveSLAViolation(UUID violationId) {
        log.info("Resolving SLA violation: {}", violationId);
        
        SLAViolation violation = slaViolationRepository.findById(violationId)
                .orElseThrow(() -> new RuntimeException("SLA Violation not found with id: " + violationId));
        
        violation.setStatus(SLAStatus.RESOLVED);
        slaViolationRepository.save(violation);
    }

    public void createViolation(UUID entityId, String entityType, SLADefinition definition, LocalDateTime createdAt) {
        LocalDateTime deadline = createdAt.plusHours(definition.getDurationHours());
        LocalDateTime now = LocalDateTime.now();
        long delayMinutes = Duration.between(deadline, now).toMinutes();
        
        SLAViolation violation = SLAViolation.builder()
                .slaDefinition(definition)
                .entityId(entityId)
                .entityType(entityType)
                .deadline(deadline)
                .violatedAt(now)
                .delayMinutes(delayMinutes)
                .status(SLAStatus.VIOLATED)
                .escalated(false)
                .build();
        
        SLAViolation saved = slaViolationRepository.save(violation);
        
        // Auto-escalate if configured
        if (definition.getAutoEscalate()) {
            UUID escalationId = escalationService.createAutoEscalation(entityId, entityType, saved.getId());
            saved.setEscalated(true);
            saved.setEscalationId(escalationId);
            slaViolationRepository.save(saved);
        }
        
        log.warn("SLA violation created for entity: {}/{}, delay: {} minutes", entityType, entityId, delayMinutes);
    }

    private SLADefinitionDto mapToDto(SLADefinition definition) {
        return SLADefinitionDto.builder()
                .id(definition.getId())
                .name(definition.getName())
                .entityType(definition.getEntityType())
                .description(definition.getDescription())
                .durationHours(definition.getDurationHours())
                .autoEscalate(definition.getAutoEscalate())
                .active(definition.getActive())
                .createdAt(definition.getCreatedAt())
                .updatedAt(definition.getUpdatedAt())
                .build();
    }

    private SLAViolationDto mapViolationToDto(SLAViolation violation) {
        return SLAViolationDto.builder()
                .id(violation.getId())
                .slaDefinitionId(violation.getSlaDefinition().getId())
                .slaDefinitionName(violation.getSlaDefinition().getName())
                .entityId(violation.getEntityId())
                .entityType(violation.getEntityType())
                .deadline(violation.getDeadline())
                .violatedAt(violation.getViolatedAt())
                .delayMinutes(violation.getDelayMinutes())
                .status(violation.getStatus())
                .escalated(violation.getEscalated())
                .escalationId(violation.getEscalationId())
                .notes(violation.getNotes())
                .createdAt(violation.getCreatedAt())
                .build();
    }
}
