package com.pfe.client.infrastructure.web.controller;

import com.pfe.client.application.dto.AddressDto;
import com.pfe.client.application.service.AddressService;
import com.pfe.commons.dto.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/clients/{clientId}/addresses")
@RequiredArgsConstructor
@Tag(name = "Addresses API", description = "Manage client addresses")
public class AddressController {

    private final AddressService addressService;

    @PostMapping
    @PreAuthorize("hasAnyRole('AGENT', 'ADMIN')")
    @Operation(summary = "Add an address for a client")
    public ResponseEntity<BaseResponse<AddressDto>> addAddress(
            @PathVariable UUID clientId,
            @Valid @RequestBody AddressDto addressDto) {
        AddressDto response = addressService.addAddress(clientId, addressDto);
        return new ResponseEntity<>(BaseResponse.success(response), HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('AGENT', 'ADMIN')")
    @Operation(summary = "Get all addresses for a client")
    public ResponseEntity<BaseResponse<List<AddressDto>>> getAddresses(@PathVariable UUID clientId) {
        return ResponseEntity.ok(BaseResponse.success(addressService.getAddressesByClientId(clientId)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('AGENT', 'ADMIN')")
    @Operation(summary = "Get an address by ID")
    public ResponseEntity<BaseResponse<AddressDto>> getAddress(@PathVariable UUID id) {
        return ResponseEntity.ok(BaseResponse.success(addressService.getAddressById(id)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update an address")
    public ResponseEntity<BaseResponse<AddressDto>> updateAddress(
            @PathVariable UUID id,
            @Valid @RequestBody AddressDto addressDto) {
        return ResponseEntity.ok(BaseResponse.success(addressService.updateAddress(id, addressDto)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete an address")
    public ResponseEntity<Void> deleteAddress(@PathVariable UUID id) {
        addressService.deleteAddress(id);
        return ResponseEntity.noContent().build();
    }
}
