package com.pfe.client.domain.repository;

import com.pfe.client.domain.model.ClientHistory;

import java.util.List;
import java.util.UUID;

public interface ClientHistoryRepository {
    ClientHistory save(ClientHistory history);

    List<ClientHistory> findByClientId(UUID clientId);

    List<ClientHistory> findAll();
}
