package com.pfe.workflow.domain.repository;

import com.pfe.workflow.domain.model.SLADefinition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SLADefinitionRepository extends JpaRepository<SLADefinition, UUID> {
    
    Optional<SLADefinition> findByName(String name);
    
    List<SLADefinition> findByEntityType(String entityType);
    
    List<SLADefinition> findByActive(Boolean active);
    
    Optional<SLADefinition> findByEntityTypeAndActive(String entityType, Boolean active);
}
