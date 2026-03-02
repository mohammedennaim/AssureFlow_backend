package com.pfe.iam.infrastructure.persistence.repository;

import com.pfe.iam.infrastructure.persistence.entity.SessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface JpaSessionRepository extends JpaRepository<SessionEntity, String> {
    Optional<SessionEntity> findByToken(String token);

    List<SessionEntity> findByUserId(String userId);

    void deleteByUserId(String userId);

    @Modifying
    @Query("DELETE FROM SessionEntity s WHERE s.expiresAt < :now")
    void deleteExpiredSessions(LocalDateTime now);
}
