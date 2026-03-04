package com.pfe.workflow.domain.repository;

import com.pfe.workflow.domain.model.SAGATransaction;

import java.util.Optional;
import java.util.UUID;

public interface SAGATransactionRepository {
    SAGATransaction save(SAGATransaction transaction);

    Optional<SAGATransaction> findById(UUID id);
}
