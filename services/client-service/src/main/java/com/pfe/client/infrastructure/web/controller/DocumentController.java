package com.pfe.client.infrastructure.web.controller;

import com.pfe.client.application.dto.DocumentResponse;
import com.pfe.client.application.service.DocumentService;
import com.pfe.commons.dto.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/clients/{clientId}/documents")
@RequiredArgsConstructor
@Tag(name = "Documents API", description = "Manage client documents")
public class DocumentController {

    private final DocumentService documentService;

    @Data
    @AllArgsConstructor
    static class DocumentUploadRequest {
        private String fileName;
        private String filePath;
    }

    @PostMapping
    @Operation(summary = "Upload document metadata for client")
    public ResponseEntity<BaseResponse<DocumentResponse>> upload(@PathVariable String clientId, @Valid @RequestBody DocumentUploadRequest request) {
        var r = documentService.saveDocument(clientId, request.getFileName(), request.getFilePath());
        return ResponseEntity.ok(BaseResponse.success(r));
    }

    @GetMapping
    public ResponseEntity<BaseResponse<List<DocumentResponse>>> list(@PathVariable String clientId) {
        return ResponseEntity.ok(BaseResponse.success(documentService.getDocumentsByClientId(clientId)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<DocumentResponse>> getById(@PathVariable String clientId, @PathVariable String id) {
        return ResponseEntity.ok(BaseResponse.success(documentService.getDocumentById(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable String clientId, @PathVariable String id) {
        documentService.deleteDocument(id);
        return ResponseEntity.ok(BaseResponse.success(null, "Deleted"));
    }
}
