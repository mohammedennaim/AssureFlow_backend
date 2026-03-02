package com.pfe.workflow.infrastructure.persistence.repository;

import com.pfe.workflow.domain.model.TaskStatus;
import com.pfe.workflow.infrastructure.persistence.entity.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaTaskRepository extends JpaRepository<TaskEntity, String> {
    List<TaskEntity> findByAssignedTo(String assignedTo);

    List<TaskEntity> findByStatus(TaskStatus status);

    List<TaskEntity> findByRelatedEntityId(String relatedEntityId);
}
