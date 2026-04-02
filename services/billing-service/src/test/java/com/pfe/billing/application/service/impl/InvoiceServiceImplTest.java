package com.pfe.billing.application.service.impl;

import com.pfe.billing.application.dto.CreateInvoiceRequest;
import com.pfe.billing.application.dto.InvoiceDto;
import com.pfe.billing.application.mapper.InvoiceMapper;
import com.pfe.billing.domain.exception.InvoiceNotFoundException;
import com.pfe.billing.domain.exception.PaymentNotFoundException;
import com.pfe.billing.domain.model.Invoice;
import com.pfe.billing.domain.model.InvoiceStatus;
import com.pfe.billing.domain.model.Payment;
import com.pfe.billing.domain.repository.InvoiceRepository;
import com.pfe.billing.domain.repository.PaymentRepository;
import com.pfe.billing.infrastructure.client.PolicyDto;
import com.pfe.billing.infrastructure.client.PolicyServiceClient;
import com.pfe.commons.exceptions.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for InvoiceServiceImpl using Mockito mocks.
 * Tests cover create, read, cancel, pay, and delete operations.
 */
@ExtendWith(MockitoExtension.class)
class InvoiceServiceImplTest {

    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private InvoiceMapper invoiceMapper;

    @Mock
    private PolicyServiceClient policyServiceClient;

    @Mock
    private com.pfe.billing.infrastructure.messaging.BillingEventPublisher billingEventPublisher;

    @InjectMocks
    private InvoiceServiceImpl invoiceService;

    private final UUID INVOICE_ID = UUID.randomUUID();
    private final UUID POLICY_ID = UUID.randomUUID();
    private final UUID CLIENT_ID = UUID.randomUUID();

    private Invoice createTestInvoice(InvoiceStatus status) {
        Invoice invoice = new Invoice();
        invoice.setId(INVOICE_ID);
        invoice.setInvoiceNumber("INV-12345678");
        invoice.setPolicyId(POLICY_ID);
        invoice.setClientId(CLIENT_ID);
        invoice.setStatus(status);
        invoice.setAmount(new BigDecimal("1000"));
        invoice.setTaxAmount(new BigDecimal("200"));
        invoice.setTotalAmount(new BigDecimal("1200"));
        invoice.setDueDate(LocalDate.now().plusMonths(1));
        return invoice;
    }

    // ===================== CREATE =====================

    @Nested
    @DisplayName("Create Invoice Tests")
    class CreateInvoiceTests {

        @Test
        @DisplayName("Should create invoice successfully")
        void shouldCreateInvoice() {
            CreateInvoiceRequest request = new CreateInvoiceRequest();
            request.setPolicyId(POLICY_ID);
            request.setClientId(CLIENT_ID);
            request.setAmount(new BigDecimal("1000"));
            request.setTaxAmount(new BigDecimal("200"));
            request.setDueDate(LocalDate.now().plusMonths(1));

            PolicyDto policyDto = new PolicyDto();
            policyDto.setPolicyNumber("POL-ABCD1234");

            Invoice invoice = createTestInvoice(InvoiceStatus.DRAFT);
            InvoiceDto dto = new InvoiceDto();
            dto.setId(INVOICE_ID);

            when(policyServiceClient.getPolicyById(POLICY_ID.toString())).thenReturn(policyDto);
            when(invoiceMapper.toDomain(request)).thenReturn(invoice);
            when(invoiceRepository.save(any(Invoice.class))).thenReturn(invoice);
            when(invoiceMapper.toDto(invoice)).thenReturn(dto);

            InvoiceDto result = invoiceService.createInvoice(request);

            assertNotNull(result);
            assertEquals(INVOICE_ID, result.getId());
            verify(invoiceRepository).save(any(Invoice.class));
        }

        @Test
        @DisplayName("Should throw when policy ID is null")
        void shouldThrowWhenPolicyIdNull() {
            CreateInvoiceRequest request = new CreateInvoiceRequest();
            request.setPolicyId(null);

            assertThrows(BusinessException.class, () -> invoiceService.createInvoice(request));
            verify(invoiceRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw when invoice amount exceeds policy premium")
        void shouldThrowWhenInvoiceAmountExceedsPremium() {
            CreateInvoiceRequest request = new CreateInvoiceRequest();
            request.setPolicyId(POLICY_ID);
            request.setClientId(CLIENT_ID);
            request.setAmount(new BigDecimal("15000")); // Amount > Premium
            request.setTaxAmount(new BigDecimal("3000"));
            request.setDueDate(LocalDate.now().plusMonths(1));

            PolicyDto policyDto = new PolicyDto();
            policyDto.setPolicyNumber("POL-ABCD1234");
            policyDto.setPremiumAmount(new BigDecimal("12000")); // Premium = 12000

            when(policyServiceClient.getPolicyById(POLICY_ID.toString())).thenReturn(policyDto);

            BusinessException exception = assertThrows(BusinessException.class, () -> invoiceService.createInvoice(request));
            assertTrue(exception.getMessage().contains("ne peut pas dépasser"));
            verify(invoiceRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should create invoice when amount is less than policy premium")
        void shouldCreateInvoiceWhenAmountLessThanPremium() {
            CreateInvoiceRequest request = new CreateInvoiceRequest();
            request.setPolicyId(POLICY_ID);
            request.setClientId(CLIENT_ID);
            request.setAmount(new BigDecimal("10000")); // Amount < Premium
            request.setTaxAmount(new BigDecimal("2000"));
            request.setDueDate(LocalDate.now().plusMonths(1));

            PolicyDto policyDto = new PolicyDto();
            policyDto.setPolicyNumber("POL-ABCD1234");
            policyDto.setPremiumAmount(new BigDecimal("12000")); // Premium = 12000

            Invoice invoice = createTestInvoice(InvoiceStatus.DRAFT);
            InvoiceDto dto = new InvoiceDto();
            dto.setId(INVOICE_ID);

            when(policyServiceClient.getPolicyById(POLICY_ID.toString())).thenReturn(policyDto);
            when(invoiceMapper.toDomain(request)).thenReturn(invoice);
            when(invoiceRepository.save(any(Invoice.class))).thenReturn(invoice);
            when(invoiceMapper.toDto(invoice)).thenReturn(dto);

            InvoiceDto result = assertDoesNotThrow(() -> invoiceService.createInvoice(request));

            assertNotNull(result);
            assertEquals(INVOICE_ID, result.getId());
            verify(invoiceRepository).save(any(Invoice.class));
        }

        @Test
        @DisplayName("Should create invoice when amount equals policy premium")
        void shouldCreateInvoiceWhenAmountEqualsPremium() {
            CreateInvoiceRequest request = new CreateInvoiceRequest();
            request.setPolicyId(POLICY_ID);
            request.setClientId(CLIENT_ID);
            request.setAmount(new BigDecimal("12000")); // Amount == Premium
            request.setTaxAmount(new BigDecimal("2400"));
            request.setDueDate(LocalDate.now().plusMonths(1));

            PolicyDto policyDto = new PolicyDto();
            policyDto.setPolicyNumber("POL-ABCD1234");
            policyDto.setPremiumAmount(new BigDecimal("12000")); // Premium = 12000

            Invoice invoice = createTestInvoice(InvoiceStatus.DRAFT);
            InvoiceDto dto = new InvoiceDto();
            dto.setId(INVOICE_ID);

            when(policyServiceClient.getPolicyById(POLICY_ID.toString())).thenReturn(policyDto);
            when(invoiceMapper.toDomain(request)).thenReturn(invoice);
            when(invoiceRepository.save(any(Invoice.class))).thenReturn(invoice);
            when(invoiceMapper.toDto(invoice)).thenReturn(dto);

            InvoiceDto result = assertDoesNotThrow(() -> invoiceService.createInvoice(request));

            assertNotNull(result);
            assertEquals(INVOICE_ID, result.getId());
            verify(invoiceRepository).save(any(Invoice.class));
        }
    }

    // ===================== READ =====================

    @Nested
    @DisplayName("Get Invoice Tests")
    class GetInvoiceTests {

        @Test
        @DisplayName("Should return invoice by ID")
        void shouldReturnInvoiceById() {
            Invoice invoice = createTestInvoice(InvoiceStatus.ACTIVE);
            InvoiceDto dto = new InvoiceDto();
            dto.setId(INVOICE_ID);

            when(invoiceRepository.findById(INVOICE_ID)).thenReturn(Optional.of(invoice));
            when(invoiceMapper.toDto(invoice)).thenReturn(dto);

            InvoiceDto result = invoiceService.getInvoiceById(INVOICE_ID);

            assertNotNull(result);
            assertEquals(INVOICE_ID, result.getId());
        }

        @Test
        @DisplayName("Should throw when invoice not found")
        void shouldThrowWhenInvoiceNotFound() {
            UUID unknownId = UUID.randomUUID();
            when(invoiceRepository.findById(unknownId)).thenReturn(Optional.empty());

            assertThrows(InvoiceNotFoundException.class, () -> invoiceService.getInvoiceById(unknownId));
        }

        @Test
        @DisplayName("Should return all invoices")
        void shouldReturnAllInvoices() {
            Invoice invoice = createTestInvoice(InvoiceStatus.ACTIVE);
            InvoiceDto dto = new InvoiceDto();

            when(invoiceRepository.findAll()).thenReturn(List.of(invoice));
            when(invoiceMapper.toDto(invoice)).thenReturn(dto);

            List<InvoiceDto> result = invoiceService.getAllInvoices();

            assertEquals(1, result.size());
        }
    }

    // ===================== OPERATIONS =====================

    @Nested
    @DisplayName("Invoice Operations Tests")
    class InvoiceOperationsTests {

        @Test
        @DisplayName("Should cancel existing invoice")
        void shouldCancelInvoice() {
            Invoice invoice = createTestInvoice(InvoiceStatus.ACTIVE);

            when(invoiceRepository.findById(INVOICE_ID)).thenReturn(Optional.of(invoice));
            when(invoiceRepository.save(any(Invoice.class))).thenReturn(invoice);

            assertDoesNotThrow(() -> invoiceService.cancelInvoice(INVOICE_ID));
            verify(invoiceRepository).save(invoice);
        }

        @Test
        @DisplayName("Should throw when cancelling non-existent invoice")
        void shouldThrowWhenCancellingNonExistent() {
            UUID unknownId = UUID.randomUUID();
            when(invoiceRepository.findById(unknownId)).thenReturn(Optional.empty());

            assertThrows(InvoiceNotFoundException.class, () -> invoiceService.cancelInvoice(unknownId));
        }

        @Test
        @DisplayName("Should mark invoice as paid")
        void shouldMarkAsPaid() {
            Invoice invoice = createTestInvoice(InvoiceStatus.ACTIVE);
            UUID paymentId = UUID.randomUUID();
            Payment payment = new Payment();
            payment.setId(paymentId);

            when(invoiceRepository.findById(INVOICE_ID)).thenReturn(Optional.of(invoice));
            when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));
            when(invoiceRepository.save(any(Invoice.class))).thenReturn(invoice);

            assertDoesNotThrow(() -> invoiceService.markAsPaid(INVOICE_ID, paymentId));
            verify(invoiceRepository).save(invoice);
        }

        @Test
        @DisplayName("Should throw when payment not found for mark as paid")
        void shouldThrowWhenPaymentNotFound() {
            Invoice invoice = createTestInvoice(InvoiceStatus.ACTIVE);
            UUID paymentId = UUID.randomUUID();

            when(invoiceRepository.findById(INVOICE_ID)).thenReturn(Optional.of(invoice));
            when(paymentRepository.findById(paymentId)).thenReturn(Optional.empty());

            assertThrows(PaymentNotFoundException.class, () -> invoiceService.markAsPaid(INVOICE_ID, paymentId));
        }

        @Test
        @DisplayName("Should delete existing invoice")
        void shouldDeleteInvoice() {
            Invoice invoice = createTestInvoice(InvoiceStatus.DRAFT);

            when(invoiceRepository.findById(INVOICE_ID)).thenReturn(Optional.of(invoice));

            assertDoesNotThrow(() -> invoiceService.deleteInvoice(INVOICE_ID));
            verify(invoiceRepository).deleteById(INVOICE_ID);
        }

        @Test
        @DisplayName("Should throw when deleting non-existent invoice")
        void shouldThrowWhenDeletingNonExistent() {
            UUID unknownId = UUID.randomUUID();
            when(invoiceRepository.findById(unknownId)).thenReturn(Optional.empty());

            assertThrows(InvoiceNotFoundException.class, () -> invoiceService.deleteInvoice(unknownId));
        }
    }
}
