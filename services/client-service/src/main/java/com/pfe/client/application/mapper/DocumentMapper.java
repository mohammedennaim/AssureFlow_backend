package com.pfe.client.application.mapper;

import com.pfe.client.application.dto.DocumentResponse;
import com.pfe.client.domain.model.Document;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface DocumentMapper {
    DocumentMapper INSTANCE = Mappers.getMapper(DocumentMapper.class);

    DocumentResponse toResponse(Document document);
}
