package com.pfe.workflow.infrastructure.persistence.repository;

import com.pfe.workflow.domain.model.Task;
import com.pfe.workflow.domain.model.TaskStatus;
import com.pfe.workflow.domain.repository.TaskRepository;
import com.pfe.workflow.infrastructure.persistence.entity.TaskEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class TaskRepositoryAdapter implements TaskRepository {

    private final JpaTaskRepository jpaTaskRepository;

    @Override
    public Task save(Task task) {
        return toDomain(jpaTaskRepository.save(toEntity(task)));
    }

    @Override
    public Optional<Task> findById(String id) {
        return jpaTaskRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Task> findByAssignedTo(String assignedTo) {
        return jpaTaskRepository.findByAssignedTo(assignedTo).stream()
                .map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Task> findByStatus(TaskStatus status) {
        return jpaTaskRepository.findByStatus(status).stream()
                .map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Task> findByRelatedEntityId(String relatedEntityId) {
        return jpaTaskRepository.findByRelatedEntityId(relatedEntityId).stream()
                .map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Task> findAll() {
        return jpaTaskRepository.findAll().stream()
                .map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        jpaTaskRepository.deleteById(id);
    }

    private Task toDomain(TaskEntity entity) {
        return Task.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .assignedTo(entity.getAssignedTo())
                .relatedEntityId(entity.getRelatedEntityId())
                .relatedEntityType(entity.getRelatedEntityType())
                .status(entity.getStatus())
                .dueDate(entity.getDueDate())
                .completedAt(entity.getCompletedAt())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private TaskEntity toEntity(Task domain) {
        return TaskEntity.builder()
                .id(domain.getId())
                .title(domain.getTitle())
                .description(domain.getDescription())
                .assignedTo(domain.getAssignedTo())
                .relatedEntityId(domain.getRelatedEntityId())
                .relatedEntityType(domain.getRelatedEntityType())
                .status(domain.getStatus())
                .dueDate(domain.getDueDate())
                .completedAt(domain.getCompletedAt())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }
}
