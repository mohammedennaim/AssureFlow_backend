package com.pfe.claims.infrastructure.persistence.repository;

import com.pfe.claims.domain.model.Claim;
import com.pfe.claims.domain.model.ClaimDocument;
import com.pfe.claims.domain.model.ClaimStatus;
import com.pfe.claims.domain.repository.ClaimRepository;
import com.pfe.claims.infrastructure.persistence.entity.ClaimDocumentEntity;
import com.pfe.claims.infrastructure.persistence.entity.ClaimEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ClaimRepositoryAdapter implements ClaimRepository {

    private final JpaClaimRepository jpaClaimRepository;

    @Override
    public Claim save(Claim claim) {
        ClaimEntity entity = toEntity(claim);
        if (entity.getDocuments() != null) {
            entity.getDocuments().forEach(d -> d.setClaim(entity));
        }
        return toDomain(jpaClaimRepository.save(entity));
    }

    @Override
    public Optional<Claim> findById(String id) {
        return jpaClaimRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Claim> findByClaimNumber(String claimNumber) {
        return jpaClaimRepository.findByClaimNumber(claimNumber).map(this::toDomain);
    }

    @Override
    public List<Claim> findByClientId(String clientId) {
        return jpaClaimRepository.findByClientId(clientId).stream()
                .map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Claim> findByPolicyId(String policyId) {
        return jpaClaimRepository.findByPolicyId(policyId).stream()
                .map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Claim> findByStatus(ClaimStatus status) {
        return jpaClaimRepository.findByStatus(status).stream()
                .map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Claim> findAll() {
        return jpaClaimRepository.findAll().stream()
                .map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        jpaClaimRepository.deleteById(id);
    }

    @Override
    public boolean existsByClaimNumber(String claimNumber) {
        return jpaClaimRepository.existsByClaimNumber(claimNumber);
    }

    private Claim toDomain(ClaimEntity entity) {
        List<ClaimDocument> documents = entity.getDocuments() == null ? List.of() :
                entity.getDocuments().stream().map(this::toDocumentDomain).collect(Collectors.toList());
        return Claim.builder()
                .id(entity.getId())
                .claimNumber(entity.getClaimNumber())
                .policyId(entity.getPolicyId())
                .clientId(entity.getClientId())
                .description(entity.getDescription())
                .status(entity.getStatus())
                .incidentDate(entity.getIncidentDate())
                .claimedAmount(entity.getClaimedAmount())
                .approvedAmount(entity.getApprovedAmount())
                .documents(documents)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private ClaimEntity toEntity(Claim domain) {
        List<ClaimDocumentEntity> documents = domain.getDocuments() == null ? List.of() :
                domain.getDocuments().stream().map(this::toDocumentEntity).collect(Collectors.toList());
        return ClaimEntity.builder()
                .id(domain.getId())
                .claimNumber(domain.getClaimNumber())
                .policyId(domain.getPolicyId())
                .clientId(domain.getClientId())
                .description(domain.getDescription())
                .status(domain.getStatus())
                .incidentDate(domain.getIncidentDate())
                .claimedAmount(domain.getClaimedAmount())
                .approvedAmount(domain.getApprovedAmount())
                .documents(documents)
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }

    private ClaimDocument toDocumentDomain(ClaimDocumentEntity entity) {
        return ClaimDocument.builder()
                .id(entity.getId())
                .claimId(entity.getClaim() != null ? entity.getClaim().getId() : null)
                .documentName(entity.getDocumentName())
                .documentUrl(entity.getDocumentUrl())
                .uploadDate(entity.getUploadDate())
                .build();
    }

    private ClaimDocumentEntity toDocumentEntity(ClaimDocument domain) {
        return ClaimDocumentEntity.builder()
                .id(domain.getId())
                .documentName(domain.getDocumentName())
                .documentUrl(domain.getDocumentUrl())
                .uploadDate(domain.getUploadDate())
                .build();
    }
}
