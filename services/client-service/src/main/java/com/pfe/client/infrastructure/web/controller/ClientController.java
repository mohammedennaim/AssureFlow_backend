package com.pfe.client.infrastructure.web.controller;

import com.pfe.client.application.dto.ClientHistoryResponse;
import com.pfe.client.application.dto.ClientRequest;
import com.pfe.client.application.dto.ClientResponse;
import com.pfe.client.application.dto.ClientSearchCriteria;
import com.pfe.client.application.service.ClientHistoryService;
import com.pfe.client.application.service.ClientService;
import com.pfe.commons.dto.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/clients")
@RequiredArgsConstructor
@Tag(name = "Clients API", description = "Endpoints for managing insurance clients")
public class ClientController {

    private final ClientService clientService;
    private final ClientHistoryService historyService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new client")
    public ResponseEntity<BaseResponse<ClientResponse>> createClient(@Valid @RequestBody ClientRequest request) {
        ClientResponse response = clientService.createClient(request);
        return new ResponseEntity<>(BaseResponse.success(response), HttpStatus.CREATED);
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('CLIENT', 'AGENT', 'ADMIN')")
    @Operation(summary = "Get current authenticated client profile")
    public ResponseEntity<BaseResponse<ClientResponse>> getCurrentClient(Authentication authentication) {
        String email = authentication.getName(); // JWT subject = email
        return ResponseEntity.ok(BaseResponse.success(clientService.getClientByEmail(email)));
    }
    @PatchMapping("/me")
    @PreAuthorize("hasAnyRole('CLIENT', 'AGENT', 'ADMIN')")
    @Operation(summary = "Update current authenticated client profile (self-service)")
    public ResponseEntity<BaseResponse<ClientResponse>> updateCurrentClient(
            Authentication authentication,
            @RequestBody ClientRequest request) {
        String email = authentication.getName();
        return ResponseEntity.ok(BaseResponse.success(clientService.updateMyProfile(email, request)));
    }
    @GetMapping
    @PreAuthorize("hasAnyRole('AGENT', 'ADMIN')")
    @Operation(summary = "Get all clients")
    public ResponseEntity<BaseResponse<List<ClientResponse>>> getAllClients(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(BaseResponse.success(clientService.getAllClients(page, size)));
    }

    @GetMapping("/{identifier}")
    @PreAuthorize("hasAnyRole('AGENT', 'ADMIN')")
    @Operation(summary = "Get a client by ID or Email")
    public ResponseEntity<BaseResponse<ClientResponse>> getClient(@PathVariable String identifier) {
        try {
            UUID id = UUID.fromString(identifier);
            return ResponseEntity.ok(BaseResponse.success(clientService.getClientById(id)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(BaseResponse.success(clientService.getClientByEmail(identifier)));
        }
    }

    @GetMapping("/email/{email}")
    @PreAuthorize("hasAnyRole('AGENT', 'ADMIN')")
    @Operation(summary = "Get a client by Email")
    public ResponseEntity<BaseResponse<ClientResponse>> getClientByEmail(@PathVariable String email) {
        return ResponseEntity.ok(BaseResponse.success(clientService.getClientByEmail(email)));
    }

    @GetMapping("/cin/{cin}")
    @PreAuthorize("hasAnyRole('AGENT', 'ADMIN')")
    @Operation(summary = "Get a client by CIN")
    public ResponseEntity<BaseResponse<ClientResponse>> getClientByCin(@PathVariable String cin) {
        return ResponseEntity.ok(BaseResponse.success(clientService.getClientByCin(cin)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update an existing client")
    public ResponseEntity<BaseResponse<ClientResponse>> updateClient(
            @PathVariable UUID id,
            @Valid @RequestBody ClientRequest request) {
        return ResponseEntity.ok(BaseResponse.success(clientService.updateClient(id, request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a client")
    public ResponseEntity<Void> deleteClient(@PathVariable UUID id) {
        clientService.deleteClient(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('AGENT', 'ADMIN')")
    @Operation(summary = "Search clients")
    public ResponseEntity<BaseResponse<List<ClientResponse>>> search(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String cin,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var criteria = ClientSearchCriteria.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .cin(cin)
                .build();
        return ResponseEntity.ok(BaseResponse.success(clientService.search(criteria, page, size)));
    }

    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Activate a client")
    public ResponseEntity<BaseResponse<Void>> activateClient(@PathVariable UUID id) {
        clientService.activateClient(id);
        return ResponseEntity.ok(BaseResponse.success(null, "Client activated"));
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Deactivate a client")
    public ResponseEntity<BaseResponse<Void>> deactivateClient(@PathVariable UUID id) {
        clientService.deactivateClient(id);
        return ResponseEntity.ok(BaseResponse.success(null, "Client deactivated"));
    }

    @GetMapping("/{id}/history")
    @PreAuthorize("hasAnyRole('AGENT', 'ADMIN')")
    @Operation(summary = "Get client history")
    public ResponseEntity<BaseResponse<List<ClientHistoryResponse>>> getClientHistory(@PathVariable UUID id) {
        return ResponseEntity.ok(BaseResponse.success(historyService.getHistoryByClientId(id)));
    }
}
