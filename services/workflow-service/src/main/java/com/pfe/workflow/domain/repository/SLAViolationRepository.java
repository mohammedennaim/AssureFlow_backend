package com.pfe.workflow.domain.repository;

import com.pfe.workflow.domain.model.SLAStatus;
import com.pfe.workflow.domain.model.SLAViolation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SLAViolationRepository extends JpaRepository<SLAViolation, UUID> {
    
    Page<SLAViolation> findByEntityTypeAndEntityId(String entityType, UUID entityId, Pageable pageable);
    
    Page<SLAViolation> findByStatus(SLAStatus status, Pageable pageable);
    
    Page<SLAViolation> findByEscalated(Boolean escalated, Pageable pageable);
    
    Optional<SLAViolation> findByEntityIdAndStatus(UUID entityId, SLAStatus status);
    
    List<SLAViolation> findByStatusAndEscalated(SLAStatus status, Boolean escalated);
}
