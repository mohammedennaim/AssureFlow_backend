package com.pfe.client.infrastructure.persistence.mapper;

import com.pfe.client.domain.model.ClientHistory;
import com.pfe.client.infrastructure.persistence.entity.ClientHistoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ClientHistoryEntityMapper {
    ClientHistoryEntityMapper INSTANCE = Mappers.getMapper(ClientHistoryEntityMapper.class);

    ClientHistoryEntity toEntity(ClientHistory history);

    ClientHistory toDomain(ClientHistoryEntity entity);

    List<ClientHistory> toDomainList(List<ClientHistoryEntity> entities);
}
