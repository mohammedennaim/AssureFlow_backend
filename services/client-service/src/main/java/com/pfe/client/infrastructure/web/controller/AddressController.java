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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/clients/{clientId}/addresses")
@RequiredArgsConstructor
@Tag(name = "Addresses API", description = "Manage client addresses")
public class AddressController {

    private final AddressService addressService;

    @PostMapping
    @Operation(summary = "Add an address for a client")
    public ResponseEntity<BaseResponse<AddressDto>> addAddress(
            @PathVariable String clientId,
            @Valid @RequestBody AddressDto addressDto) {
        AddressDto response = addressService.addAddress(clientId, addressDto);
        return new ResponseEntity<>(BaseResponse.success(response), HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get all addresses for a client")
    public ResponseEntity<BaseResponse<List<AddressDto>>> getAddresses(@PathVariable String clientId) {
        return ResponseEntity.ok(BaseResponse.success(addressService.getAddressesByClientId(clientId)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get an address by ID")
    public ResponseEntity<BaseResponse<AddressDto>> getAddress(@PathVariable String id) {
        return ResponseEntity.ok(BaseResponse.success(addressService.getAddressById(id)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an address")
    public ResponseEntity<BaseResponse<AddressDto>> updateAddress(
            @PathVariable String id,
            @Valid @RequestBody AddressDto addressDto) {
        return ResponseEntity.ok(BaseResponse.success(addressService.updateAddress(id, addressDto)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an address")
    public ResponseEntity<Void> deleteAddress(@PathVariable String id) {
        addressService.deleteAddress(id);
        return ResponseEntity.noContent().build();
    }
}
