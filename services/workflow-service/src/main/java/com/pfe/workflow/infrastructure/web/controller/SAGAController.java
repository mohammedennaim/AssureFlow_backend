package com.pfe.workflow.infrastructure.web.controller;

import com.pfe.workflow.application.dto.SAGATransactionDto;
import com.pfe.workflow.application.service.SAGAOrchestratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/sagas")
@RequiredArgsConstructor
public class SAGAController {

    private final SAGAOrchestratorService sagaService;

    @GetMapping
    public ResponseEntity<Page<SAGATransactionDto>> getAllSagas(Pageable pageable) {
        return ResponseEntity.ok(sagaService.getAllSagas(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SAGATransactionDto> getSagaStatus(@PathVariable UUID id) {
        return ResponseEntity.ok(sagaService.getSagaStatus(id));
    }

    @PostMapping("/start")
    public ResponseEntity<SAGATransactionDto> startSaga(
            @RequestParam String sagaType,
            @RequestParam UUID initiatedBy) {
        return ResponseEntity.ok(sagaService.startSaga(sagaType, initiatedBy));
    }

    @PostMapping("/{sagaId}/steps/{stepId}/success")
    public ResponseEntity<Void> reportStepSuccess(
            @PathVariable UUID sagaId,
            @PathVariable UUID stepId) {
        sagaService.reportStepSuccess(sagaId, stepId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{sagaId}/steps/{stepId}/failure")
    public ResponseEntity<Void> reportStepFailure(
            @PathVariable UUID sagaId,
            @PathVariable UUID stepId,
            @RequestBody String errorDetails) {
        sagaService.reportStepFailure(sagaId, stepId, errorDetails);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{sagaId}/steps/{stepId}/compensate")
    public ResponseEntity<Void> reportCompensationSuccess(
            @PathVariable UUID sagaId,
            @PathVariable UUID stepId) {
        sagaService.reportCompensationSuccess(sagaId, stepId);
        return ResponseEntity.ok().build();
    }
}
