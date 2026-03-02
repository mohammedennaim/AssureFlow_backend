package com.pfe.client.infrastructure.persistence.mapper;

import com.pfe.client.domain.model.Document;
import com.pfe.client.infrastructure.persistence.entity.DocumentEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface DocumentEntityMapper {
    DocumentEntityMapper INSTANCE = Mappers.getMapper(DocumentEntityMapper.class);

    DocumentEntity toEntity(Document document);

    Document toDomain(DocumentEntity entity);
}
