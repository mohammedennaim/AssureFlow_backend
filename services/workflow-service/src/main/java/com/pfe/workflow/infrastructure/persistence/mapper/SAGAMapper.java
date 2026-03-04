package com.pfe.workflow.infrastructure.persistence.mapper;

import com.pfe.workflow.domain.model.SAGAStep;
import com.pfe.workflow.domain.model.SAGATransaction;
import com.pfe.workflow.infrastructure.persistence.entity.SAGAStepEntity;
import com.pfe.workflow.infrastructure.persistence.entity.SAGATransactionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SAGAMapper {

    SAGATransaction toDomain(SAGATransactionEntity entity);

    SAGATransactionEntity toEntity(SAGATransaction domain);

    SAGAStep toDomain(SAGAStepEntity entity);

    @Mapping(target = "transaction", ignore = true)
    SAGAStepEntity toEntity(SAGAStep domain);
}
