package com.pfe.client.application.service;

import com.pfe.client.application.dto.DocumentResponse;

import java.util.List;
import java.util.UUID;

public interface DocumentService {
    DocumentResponse saveDocument(UUID clientId, String fileName, String filePath);

    DocumentResponse getDocumentById(UUID id);

    List<DocumentResponse> getDocumentsByClientId(UUID clientId);

    void deleteDocument(UUID id);
}
