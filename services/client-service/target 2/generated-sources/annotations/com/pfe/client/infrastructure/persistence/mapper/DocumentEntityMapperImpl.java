package com.pfe.client.infrastructure.persistence.mapper;

import com.pfe.client.domain.model.Document;
import com.pfe.client.infrastructure.persistence.entity.DocumentEntity;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-14T22:53:30+0000",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.45.0.v20260224-0835, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class DocumentEntityMapperImpl implements DocumentEntityMapper {

    @Override
    public DocumentEntity toEntity(Document document) {
        if ( document == null ) {
            return null;
        }

        DocumentEntity.DocumentEntityBuilder documentEntity = DocumentEntity.builder();

        documentEntity.clientId( document.getClientId() );
        documentEntity.documentType( document.getDocumentType() );
        documentEntity.fileName( document.getFileName() );
        documentEntity.filePath( document.getFilePath() );
        documentEntity.id( document.getId() );
        documentEntity.uploadedAt( document.getUploadedAt() );

        return documentEntity.build();
    }

    @Override
    public Document toDomain(DocumentEntity entity) {
        if ( entity == null ) {
            return null;
        }

        Document.DocumentBuilder document = Document.builder();

        document.clientId( entity.getClientId() );
        document.documentType( entity.getDocumentType() );
        document.fileName( entity.getFileName() );
        document.filePath( entity.getFilePath() );
        document.id( entity.getId() );
        document.uploadedAt( entity.getUploadedAt() );

        return document.build();
    }
}
