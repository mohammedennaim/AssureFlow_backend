package com.pfe.client.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientHistory {
    private String id;
    private String clientId;
    private String action;
    private LocalDateTime performedAt;
    private String performedBy;
}
