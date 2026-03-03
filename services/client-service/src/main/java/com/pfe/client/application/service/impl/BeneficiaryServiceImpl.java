package com.pfe.client.application.service.impl;

import com.pfe.client.application.dto.BeneficiaryRequest;
import com.pfe.client.application.dto.BeneficiaryResponse;
import com.pfe.client.application.mapper.BeneficiaryMapper;
import com.pfe.client.application.service.BeneficiaryService;
import com.pfe.client.domain.model.Beneficiary;
import com.pfe.client.domain.repository.BeneficiaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BeneficiaryServiceImpl implements BeneficiaryService {

    private final BeneficiaryRepository beneficiaryRepository;
    private final BeneficiaryMapper mapper;

    @Override
    @Transactional
    public BeneficiaryResponse createBeneficiary(UUID clientId, BeneficiaryRequest request) {
        Beneficiary b = mapper.toDomain(request);
        b.setClientId(clientId);
        var saved = beneficiaryRepository.save(b);
        return mapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public BeneficiaryResponse getBeneficiaryById(UUID id) {
        var b = beneficiaryRepository.findById(id).orElseThrow(() -> new RuntimeException("Beneficiary not found"));
        return mapper.toResponse(b);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BeneficiaryResponse> getBeneficiariesByClientId(UUID clientId) {
        return beneficiaryRepository.findByClientId(clientId).stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteBeneficiary(UUID id) {
        beneficiaryRepository.deleteById(id);
    }
}
