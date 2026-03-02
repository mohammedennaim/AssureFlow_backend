package com.pfe.billing.application.service;

import com.pfe.billing.application.dto.InvoiceRequest;
import com.pfe.billing.application.dto.InvoiceResponse;
import com.pfe.billing.application.dto.PaymentRequest;
import com.pfe.billing.application.dto.PaymentResponse;
import com.pfe.billing.domain.model.InvoiceStatus;

import java.util.List;

public interface BillingService {
    InvoiceResponse createInvoice(InvoiceRequest request);

    InvoiceResponse getInvoiceById(String id);

    List<InvoiceResponse> getInvoicesByClientId(String clientId);

    List<InvoiceResponse> getInvoicesByPolicyId(String policyId);

    List<InvoiceResponse> getInvoicesByStatus(InvoiceStatus status);

    List<InvoiceResponse> getAllInvoices();

    PaymentResponse addPayment(PaymentRequest request);

    InvoiceResponse markInvoiceAsOverdue(String id);

    void deleteInvoice(String id);
}
