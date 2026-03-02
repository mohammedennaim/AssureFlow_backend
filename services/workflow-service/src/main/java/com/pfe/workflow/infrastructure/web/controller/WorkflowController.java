package com.pfe.workflow.infrastructure.web.controller;

import com.pfe.workflow.application.dto.TaskRequest;
import com.pfe.workflow.application.dto.TaskResponse;
import com.pfe.workflow.application.service.WorkflowService;
import com.pfe.workflow.domain.model.TaskStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/workflows")
@RequiredArgsConstructor
@Tag(name = "Workflow API", description = "Endpoints for managing workflow tasks")
public class WorkflowController {

    private final WorkflowService workflowService;

    @PostMapping("/tasks")
    @Operation(summary = "Create a new task")
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody TaskRequest request) {
        return new ResponseEntity<>(workflowService.createTask(request), HttpStatus.CREATED);
    }

    @GetMapping("/tasks")
    @Operation(summary = "Get all tasks")
    public ResponseEntity<List<TaskResponse>> getAllTasks() {
        return ResponseEntity.ok(workflowService.getAllTasks());
    }

    @GetMapping("/tasks/{id}")
    @Operation(summary = "Get task by ID")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable String id) {
        return ResponseEntity.ok(workflowService.getTaskById(id));
    }

    @GetMapping("/tasks/assignee/{assignedTo}")
    @Operation(summary = "Get tasks by assignee")
    public ResponseEntity<List<TaskResponse>> getTasksByAssignee(@PathVariable String assignedTo) {
        return ResponseEntity.ok(workflowService.getTasksByAssignee(assignedTo));
    }

    @GetMapping("/tasks/status/{status}")
    @Operation(summary = "Get tasks by status")
    public ResponseEntity<List<TaskResponse>> getTasksByStatus(@PathVariable TaskStatus status) {
        return ResponseEntity.ok(workflowService.getTasksByStatus(status));
    }

    @GetMapping("/tasks/entity/{relatedEntityId}")
    @Operation(summary = "Get tasks by related entity")
    public ResponseEntity<List<TaskResponse>> getTasksByEntity(@PathVariable String relatedEntityId) {
        return ResponseEntity.ok(workflowService.getTasksByRelatedEntity(relatedEntityId));
    }

    @PatchMapping("/tasks/{id}/start")
    @Operation(summary = "Start a task")
    public ResponseEntity<TaskResponse> startTask(@PathVariable String id) {
        return ResponseEntity.ok(workflowService.startTask(id));
    }

    @PatchMapping("/tasks/{id}/complete")
    @Operation(summary = "Complete a task")
    public ResponseEntity<TaskResponse> completeTask(@PathVariable String id) {
        return ResponseEntity.ok(workflowService.completeTask(id));
    }

    @PatchMapping("/tasks/{id}/cancel")
    @Operation(summary = "Cancel a task")
    public ResponseEntity<TaskResponse> cancelTask(@PathVariable String id) {
        return ResponseEntity.ok(workflowService.cancelTask(id));
    }

    @DeleteMapping("/tasks/{id}")
    @Operation(summary = "Delete a task")
    public ResponseEntity<Void> deleteTask(@PathVariable String id) {
        workflowService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}
