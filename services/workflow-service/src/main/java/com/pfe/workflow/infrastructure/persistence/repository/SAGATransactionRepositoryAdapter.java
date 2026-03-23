package com.pfe.workflow.infrastructure.persistence.repository;

import com.pfe.workflow.domain.model.SAGATransaction;
import com.pfe.workflow.domain.repository.SAGATransactionRepository;
import com.pfe.workflow.infrastructure.persistence.entity.SAGAStepEntity;
import com.pfe.workflow.infrastructure.persistence.entity.SAGATransactionEntity;
import com.pfe.workflow.infrastructure.persistence.mapper.SAGAMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SAGATransactionRepositoryAdapter implements SAGATransactionRepository {

    private final JpaSAGATransactionRepository jpaRepository;
    private final SAGAMapper mapper;

    @Override
    public SAGATransaction save(SAGATransaction transaction) {
        SAGATransactionEntity entity = mapper.toEntity(transaction);

        if (entity.getSteps() != null) {
            for (SAGAStepEntity step : entity.getSteps()) {
                step.setTransaction(entity);
            }
        }

        SAGATransactionEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<SAGATransaction> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Page<SAGATransaction> findAll(Pageable pageable) {
        return jpaRepository.findAll(pageable).map(mapper::toDomain);
    }
}
