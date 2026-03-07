package com.pfe.billing.application.service;

import com.pfe.billing.application.dto.CreateInvoiceRequest;
import com.pfe.billing.application.dto.InvoiceDto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface InvoiceService {

    InvoiceDto createInvoice(CreateInvoiceRequest request);

    InvoiceDto getInvoiceById(UUID id);

    InvoiceDto getInvoiceByNumber(String invoiceNumber);

    List<InvoiceDto> getInvoicesByClientId(UUID clientId);

    List<InvoiceDto> getInvoicesByPolicyId(UUID policyId);

    List<InvoiceDto> getAllInvoices();

    Page<InvoiceDto> getAllInvoicesPaged(int page, int size);

    void cancelInvoice(UUID id);

    void markAsPaid(UUID invoiceId, UUID paymentId);

    void deleteInvoice(UUID id);
}
