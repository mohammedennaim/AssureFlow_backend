package com.pfe.workflow.domain.repository;

import com.pfe.workflow.domain.model.SAGATransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface SAGATransactionRepository {
    SAGATransaction save(SAGATransaction transaction);

    Optional<SAGATransaction> findById(UUID id);
    
    Page<SAGATransaction> findAll(Pageable pageable);
}
