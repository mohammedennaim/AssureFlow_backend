package com.pfe.client.infrastructure.persistence.repository;

import com.pfe.client.domain.model.Beneficiary;
import com.pfe.client.domain.repository.BeneficiaryRepository;
import com.pfe.client.infrastructure.persistence.mapper.BeneficiaryEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class BeneficiaryRepositoryAdapter implements BeneficiaryRepository {

    private final JpaBeneficiaryRepository jpaBeneficiaryRepository;
    private final BeneficiaryEntityMapper mapper;

    @Override
    public Beneficiary save(Beneficiary beneficiary) {
        var entity = mapper.toEntity(beneficiary);
        var saved = jpaBeneficiaryRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Beneficiary> findById(UUID id) {
        return jpaBeneficiaryRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Beneficiary> findByClientId(UUID clientId) {
        return jpaBeneficiaryRepository.findByClientId(clientId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        jpaBeneficiaryRepository.deleteById(id);
    }

    @Override
    public void deleteByClientId(UUID clientId) {
        var list = jpaBeneficiaryRepository.findByClientId(clientId);
        jpaBeneficiaryRepository.deleteAll(list);
    }
}
