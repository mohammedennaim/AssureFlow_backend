package com.pfe.billing.application.service;

import com.pfe.billing.application.dto.CreatePaymentRequest;
import com.pfe.billing.application.dto.PaymentDto;

import java.util.List;
import java.util.UUID;

public interface PaymentService {

    PaymentDto createPayment(CreatePaymentRequest request);

    PaymentDto getPaymentById(UUID id);

    List<PaymentDto> getPaymentsByInvoiceId(UUID invoiceId);

    List<PaymentDto> getPaymentsByClientId(UUID clientId);

    List<PaymentDto> getAllPayments();

    void deletePayment(UUID id);
}
