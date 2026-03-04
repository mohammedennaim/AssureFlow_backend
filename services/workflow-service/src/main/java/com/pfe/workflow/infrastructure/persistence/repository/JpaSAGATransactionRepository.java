package com.pfe.workflow.infrastructure.persistence.repository;

import com.pfe.workflow.infrastructure.persistence.entity.SAGATransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface JpaSAGATransactionRepository extends JpaRepository<SAGATransactionEntity, UUID> {
}
