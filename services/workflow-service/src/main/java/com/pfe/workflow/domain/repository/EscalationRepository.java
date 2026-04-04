package com.pfe.workflow.domain.repository;

import com.pfe.workflow.domain.model.Escalation;
import com.pfe.workflow.domain.model.EscalationLevel;
import com.pfe.workflow.domain.model.EscalationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EscalationRepository extends JpaRepository<Escalation, UUID> {
    
    Page<Escalation> findByEntityTypeAndEntityId(String entityType, UUID entityId, Pageable pageable);
    
    Page<Escalation> findByStatus(EscalationStatus status, Pageable pageable);
    
    Page<Escalation> findByAssignedTo(UUID assignedTo, Pageable pageable);
    
    Page<Escalation> findByLevel(EscalationLevel level, Pageable pageable);
    
    List<Escalation> findBySlaViolationId(UUID slaViolationId);
    
    Page<Escalation> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
