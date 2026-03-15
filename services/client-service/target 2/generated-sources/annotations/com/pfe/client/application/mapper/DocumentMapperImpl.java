package com.pfe.client.application.mapper;

import com.pfe.client.application.dto.DocumentResponse;
import com.pfe.client.domain.model.Document;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-14T22:53:30+0000",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.45.0.v20260224-0835, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class DocumentMapperImpl implements DocumentMapper {

    @Override
    public DocumentResponse toResponse(Document document) {
        if ( document == null ) {
            return null;
        }

        DocumentResponse.DocumentResponseBuilder documentResponse = DocumentResponse.builder();

        documentResponse.documentType( document.getDocumentType() );
        documentResponse.fileName( document.getFileName() );
        documentResponse.filePath( document.getFilePath() );
        documentResponse.id( document.getId() );
        documentResponse.uploadedAt( document.getUploadedAt() );

        return documentResponse.build();
    }
}
