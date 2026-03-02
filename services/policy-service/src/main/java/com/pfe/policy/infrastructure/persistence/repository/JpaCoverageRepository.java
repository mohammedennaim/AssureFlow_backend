package com.pfe.policy.infrastructure.persistence.repository;

import com.pfe.policy.infrastructure.persistence.entity.CoverageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaCoverageRepository extends JpaRepository<CoverageEntity, String> {
    List<CoverageEntity> findByPolicyId(String policyId);
}
