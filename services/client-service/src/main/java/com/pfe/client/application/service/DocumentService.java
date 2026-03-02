package com.pfe.client.application.service;

import com.pfe.client.application.dto.DocumentResponse;

import java.util.List;

public interface DocumentService {
    DocumentResponse saveDocument(String clientId, String fileName, String filePath);

    DocumentResponse getDocumentById(String id);

    List<DocumentResponse> getDocumentsByClientId(String clientId);

    void deleteDocument(String id);
}
