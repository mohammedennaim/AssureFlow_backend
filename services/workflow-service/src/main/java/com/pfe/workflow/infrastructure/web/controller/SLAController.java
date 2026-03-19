package com.pfe.workflow.infrastructure.web.controller;

import com.pfe.workflow.application.dto.CreateSLADefinitionRequest;
import com.pfe.workflow.application.dto.SLADefinitionDto;
import com.pfe.workflow.application.dto.SLAViolationDto;
import com.pfe.workflow.application.service.SLAService;
import com.pfe.workflow.domain.model.SLAStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/sla")
@RequiredArgsConstructor
public class SLAController {

    private final SLAService slaService;

    @PostMapping("/definitions")
    public ResponseEntity<SLADefinitionDto> createSLADefinition(@Valid @RequestBody CreateSLADefinitionRequest request) {
        SLADefinitionDto created = slaService.createSLADefinition(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/definitions")
    public ResponseEntity<List<SLADefinitionDto>> getAllSLADefinitions() {
        List<SLADefinitionDto> definitions = slaService.getAllSLADefinitions();
        return ResponseEntity.ok(definitions);
    }

    @GetMapping("/definitions/{id}")
    public ResponseEntity<SLADefinitionDto> getSLADefinitionById(@PathVariable UUID id) {
        SLADefinitionDto definition = slaService.getSLADefinitionById(id);
        return ResponseEntity.ok(definition);
    }

    @GetMapping("/definitions/active")
    public ResponseEntity<List<SLADefinitionDto>> getActiveSLADefinitions() {
        List<SLADefinitionDto> definitions = slaService.getActiveSLADefinitions();
        return ResponseEntity.ok(definitions);
    }

    @GetMapping("/definitions/entity-type/{entityType}")
    public ResponseEntity<SLADefinitionDto> getSLADefinitionByEntityType(@PathVariable String entityType) {
        SLADefinitionDto definition = slaService.getSLADefinitionByEntityType(entityType);
        return ResponseEntity.ok(definition);
    }

    @GetMapping("/violations")
    public ResponseEntity<Page<SLAViolationDto>> getAllViolations(Pageable pageable) {
        Page<SLAViolationDto> violations = slaService.getAllViolations(pageable);
        return ResponseEntity.ok(violations);
    }

    @GetMapping("/violations/entity/{entityId}")
    public ResponseEntity<Page<SLAViolationDto>> getViolationsByEntity(
            @PathVariable UUID entityId,
            Pageable pageable) {
        Page<SLAViolationDto> violations = slaService.getViolationsByEntity(entityId, pageable);
        return ResponseEntity.ok(violations);
    }

    @GetMapping("/violations/status/{status}")
    public ResponseEntity<Page<SLAViolationDto>> getViolationsByStatus(
            @PathVariable SLAStatus status,
            Pageable pageable) {
        Page<SLAViolationDto> violations = slaService.getViolationsByStatus(status, pageable);
        return ResponseEntity.ok(violations);
    }

    @PostMapping("/violations/{id}/resolve")
    public ResponseEntity<Void> resolveSLAViolation(@PathVariable UUID id) {
        slaService.resolveSLAViolation(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/check")
    public ResponseEntity<Void> checkSLAViolations() {
        slaService.checkSLAViolations();
        return ResponseEntity.ok().build();
    }
}
