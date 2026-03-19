package com.pfe.workflow.infrastructure.web.controller;

import com.pfe.workflow.application.dto.CreateEscalationRequest;
import com.pfe.workflow.application.dto.EscalationDto;
import com.pfe.workflow.application.dto.ResolveEscalationRequest;
import com.pfe.workflow.application.service.EscalationService;
import com.pfe.workflow.domain.model.EscalationStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/escalations")
@RequiredArgsConstructor
public class EscalationController {

    private final EscalationService escalationService;

    @PostMapping
    public ResponseEntity<EscalationDto> createEscalation(@Valid @RequestBody CreateEscalationRequest request) {
        EscalationDto created = escalationService.createEscalation(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<Page<EscalationDto>> getAllEscalations(Pageable pageable) {
        Page<EscalationDto> escalations = escalationService.getAllEscalations(pageable);
        return ResponseEntity.ok(escalations);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EscalationDto> getEscalationById(@PathVariable UUID id) {
        EscalationDto escalation = escalationService.getEscalationById(id);
        return ResponseEntity.ok(escalation);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<Page<EscalationDto>> getEscalationsByStatus(
            @PathVariable EscalationStatus status,
            Pageable pageable) {
        Page<EscalationDto> escalations = escalationService.getEscalationsByStatus(status, pageable);
        return ResponseEntity.ok(escalations);
    }

    @GetMapping("/assigned/{userId}")
    public ResponseEntity<Page<EscalationDto>> getEscalationsByAssignedTo(
            @PathVariable UUID userId,
            Pageable pageable) {
        Page<EscalationDto> escalations = escalationService.getEscalationsByAssignedTo(userId, pageable);
        return ResponseEntity.ok(escalations);
    }

    @PostMapping("/{id}/resolve")
    public ResponseEntity<EscalationDto> resolveEscalation(
            @PathVariable UUID id,
            @Valid @RequestBody ResolveEscalationRequest request) {
        EscalationDto resolved = escalationService.resolveEscalation(id, request);
        return ResponseEntity.ok(resolved);
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<EscalationDto> cancelEscalation(@PathVariable UUID id) {
        EscalationDto cancelled = escalationService.cancelEscalation(id);
        return ResponseEntity.ok(cancelled);
    }
}
