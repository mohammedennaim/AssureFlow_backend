package com.pfe.workflow.application.service.impl;

import com.pfe.workflow.application.dto.TaskRequest;
import com.pfe.workflow.application.dto.TaskResponse;
import com.pfe.workflow.application.service.WorkflowService;
import com.pfe.workflow.domain.model.Task;
import com.pfe.workflow.domain.model.TaskStatus;
import com.pfe.workflow.domain.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkflowServiceImpl implements WorkflowService {

    private final TaskRepository taskRepository;

    @Override
    @Transactional
    public TaskResponse createTask(TaskRequest request) {
        log.info("Creating task: {}", request.getTitle());
        Task task = Task.builder()
                .id(UUID.randomUUID().toString())
                .title(request.getTitle())
                .description(request.getDescription())
                .assignedTo(request.getAssignedTo())
                .relatedEntityId(request.getRelatedEntityId())
                .relatedEntityType(request.getRelatedEntityType())
                .status(TaskStatus.PENDING)
                .dueDate(request.getDueDate())
                .build();
        return toResponse(taskRepository.save(task));
    }

    @Override
    @Transactional(readOnly = true)
    public TaskResponse getTaskById(String id) {
        return toResponse(taskRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Task not found: " + id)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> getTasksByAssignee(String assignedTo) {
        return taskRepository.findByAssignedTo(assignedTo).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> getTasksByStatus(TaskStatus status) {
        return taskRepository.findByStatus(status).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> getTasksByRelatedEntity(String relatedEntityId) {
        return taskRepository.findByRelatedEntityId(relatedEntityId).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> getAllTasks() {
        return taskRepository.findAll().stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TaskResponse startTask(String id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Task not found: " + id));
        task.start();
        return toResponse(taskRepository.save(task));
    }

    @Override
    @Transactional
    public TaskResponse completeTask(String id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Task not found: " + id));
        task.complete();
        return toResponse(taskRepository.save(task));
    }

    @Override
    @Transactional
    public TaskResponse cancelTask(String id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Task not found: " + id));
        task.cancel();
        return toResponse(taskRepository.save(task));
    }

    @Override
    @Transactional
    public void deleteTask(String id) {
        if (taskRepository.findById(id).isEmpty()) {
            throw new NoSuchElementException("Task not found: " + id);
        }
        taskRepository.deleteById(id);
    }

    private TaskResponse toResponse(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .assignedTo(task.getAssignedTo())
                .relatedEntityId(task.getRelatedEntityId())
                .relatedEntityType(task.getRelatedEntityType())
                .status(task.getStatus() != null ? task.getStatus().name() : null)
                .dueDate(task.getDueDate())
                .completedAt(task.getCompletedAt())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }
}
