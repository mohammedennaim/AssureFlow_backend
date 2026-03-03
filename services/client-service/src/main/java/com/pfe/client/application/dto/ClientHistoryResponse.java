package com.pfe.client.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientHistoryResponse {
    private UUID id;
    private UUID clientId;
    private String action;
    private LocalDateTime performedAt;
    private UUID performedBy;
}
