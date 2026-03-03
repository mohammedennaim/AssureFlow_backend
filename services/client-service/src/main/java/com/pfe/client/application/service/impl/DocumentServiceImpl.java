package com.pfe.client.application.service.impl;

import com.pfe.client.application.dto.DocumentResponse;
import com.pfe.client.application.mapper.DocumentMapper;
import com.pfe.client.application.service.DocumentService;
import com.pfe.client.domain.model.Document;
import com.pfe.client.domain.model.DocumentType;
import com.pfe.client.domain.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;
    private final DocumentMapper mapper;

    @Override
    @Transactional
    public DocumentResponse saveDocument(UUID clientId, String fileName, String filePath) {
        Document d = Document.builder()
                .clientId(clientId)
                .fileName(fileName)
                .filePath(filePath)
                .documentType(DocumentType.OTHER)
                .uploadedAt(LocalDateTime.now())
                .build();
        var saved = documentRepository.save(d);
        return mapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public DocumentResponse getDocumentById(UUID id) {
        var d = documentRepository.findById(id).orElseThrow(() -> new RuntimeException("Document not found"));
        return mapper.toResponse(d);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentResponse> getDocumentsByClientId(UUID clientId) {
        return documentRepository.findByClientId(clientId).stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteDocument(UUID id) {
        documentRepository.deleteById(id);
    }
}
