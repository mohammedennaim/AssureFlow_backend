package com.pfe.client.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientHistoryResponse {
    private String id;
    private String clientId;
    private String action;
    private LocalDateTime performedAt;
    private String performedBy;
}
