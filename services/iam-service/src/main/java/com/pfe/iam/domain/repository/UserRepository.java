package com.pfe.iam.domain.repository;

import com.pfe.iam.domain.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    Optional<User> findById(String id);

    Optional<User> findByEmail(String email);

    User save(User user);

    boolean existsByEmail(String email);

    List<User> findAll();

    void deleteById(String id);
}
