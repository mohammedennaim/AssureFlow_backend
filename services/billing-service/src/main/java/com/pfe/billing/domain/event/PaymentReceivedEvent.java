package com.pfe.billing.domain.event;

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
public class PaymentReceivedEvent {

    private UUID paymentId;
    private UUID invoiceId;
    private UUID correlationId;
    private LocalDateTime eventTimestamp;
}
