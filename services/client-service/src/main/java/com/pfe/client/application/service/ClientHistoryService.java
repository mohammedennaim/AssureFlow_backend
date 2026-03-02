package com.pfe.client.application.service;

import com.pfe.client.application.dto.ClientHistoryResponse;

import java.util.List;

public interface ClientHistoryService {
    void recordHistory(String clientId, String action, String performedBy);

    List<ClientHistoryResponse> getHistoryByClientId(String clientId);
}
