package com.pfe.iam.domain.repository;

import com.pfe.iam.domain.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    Optional<User> findById(UUID id);

    Optional<User> findByEmail(String email);

    User save(User user);

    boolean existsByEmail(String email);

    List<User> findAll();

    void deleteById(UUID id);
}
