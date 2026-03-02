package com.pfe.client.domain.repository;

import com.pfe.client.domain.model.ClientHistory;

import java.util.List;

public interface ClientHistoryRepository {
    ClientHistory save(ClientHistory history);

    List<ClientHistory> findByClientId(String clientId);

    List<ClientHistory> findAll();
}
