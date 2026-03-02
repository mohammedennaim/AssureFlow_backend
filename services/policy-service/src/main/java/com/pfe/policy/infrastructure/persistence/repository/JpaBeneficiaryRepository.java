package com.pfe.policy.infrastructure.persistence.repository;

import com.pfe.policy.infrastructure.persistence.entity.BeneficiaryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaBeneficiaryRepository extends JpaRepository<BeneficiaryEntity, String> {
    List<BeneficiaryEntity> findByPolicyId(String policyId);
}
