package com.pfe.client.application.service;

import com.pfe.client.application.dto.ClientHistoryResponse;

import java.util.List;
import java.util.UUID;

public interface ClientHistoryService {
    void recordHistory(UUID clientId, String action, String performedBy);

    List<ClientHistoryResponse> getHistoryByClientId(UUID clientId);
}
