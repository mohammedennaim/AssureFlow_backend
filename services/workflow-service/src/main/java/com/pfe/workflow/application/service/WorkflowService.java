package com.pfe.workflow.application.service;

import com.pfe.workflow.application.dto.TaskRequest;
import com.pfe.workflow.application.dto.TaskResponse;
import com.pfe.workflow.domain.model.TaskStatus;

import java.util.List;

public interface WorkflowService {
    TaskResponse createTask(TaskRequest request);

    TaskResponse getTaskById(String id);

    List<TaskResponse> getTasksByAssignee(String assignedTo);

    List<TaskResponse> getTasksByStatus(TaskStatus status);

    List<TaskResponse> getTasksByRelatedEntity(String relatedEntityId);

    List<TaskResponse> getAllTasks();

    TaskResponse startTask(String id);

    TaskResponse completeTask(String id);

    TaskResponse cancelTask(String id);

    void deleteTask(String id);
}
