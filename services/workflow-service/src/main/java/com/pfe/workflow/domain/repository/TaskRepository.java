package com.pfe.workflow.domain.repository;

import com.pfe.workflow.domain.model.Task;
import com.pfe.workflow.domain.model.TaskStatus;

import java.util.List;
import java.util.Optional;

public interface TaskRepository {
    Task save(Task task);

    Optional<Task> findById(String id);

    List<Task> findByAssignedTo(String assignedTo);

    List<Task> findByStatus(TaskStatus status);

    List<Task> findByRelatedEntityId(String relatedEntityId);

    List<Task> findAll();

    void deleteById(String id);
}
