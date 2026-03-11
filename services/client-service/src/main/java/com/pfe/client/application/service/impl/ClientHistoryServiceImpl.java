package com.pfe.client.application.service.impl;

import com.pfe.client.application.dto.ClientHistoryResponse;
import com.pfe.client.application.mapper.ClientHistoryMapper;
import com.pfe.client.application.service.ClientHistoryService;
import com.pfe.client.domain.model.ClientHistory;
import com.pfe.client.domain.repository.ClientHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClientHistoryServiceImpl implements ClientHistoryService {

    private final ClientHistoryRepository clientHistoryRepository;
    private final ClientHistoryMapper mapper;

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    @Transactional
    public void recordHistory(UUID clientId, String action, String performedBy) {
        ClientHistory history = ClientHistory.builder()
                .clientId(clientId)
                .action(action)
                .performedAt(LocalDateTime.now())
                .build();
        clientHistoryRepository.save(history);
        log.debug("Recorded history for client {}: {}", clientId, action);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT', 'CLIENT')")
    @Transactional(readOnly = true)
    public List<ClientHistoryResponse> getHistoryByClientId(UUID clientId) {
        return mapper.toResponseList(clientHistoryRepository.findByClientId(clientId));
    }
}
