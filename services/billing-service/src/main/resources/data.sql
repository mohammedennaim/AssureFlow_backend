-- ============================================================
-- Billing Service - Fake Data
-- InvoiceEntity: UUID id/policyId/clientId, has createdAt/updatedAt
-- PaymentEntity: UUID id/invoiceId/clientId, method (enum), status (enum),
--                transactionId (String), NO policyId, NO payment_date
-- ============================================================

-- Invoices
INSERT INTO invoices (id, invoice_number, policy_id, client_id, amount, tax_amount, total_amount, due_date, status, paid_direct, created_at, updated_at) VALUES
  ('44444444-0000-0000-0000-000000000001', 'INV-2024-0001', 'dddddddd-0000-0000-0000-000000000001', 'cccccccc-0000-0000-0000-000000000001', 500.00, 100.00, 600.00,  '2024-01-31', 'PAID',    false, NOW(), NOW()),
  ('44444444-0000-0000-0000-000000000002', 'INV-2024-0002', 'dddddddd-0000-0000-0000-000000000002', 'cccccccc-0000-0000-0000-000000000001', 700.00, 140.00, 840.00,  '2024-03-31', 'PAID',    false, NOW(), NOW()),
  ('44444444-0000-0000-0000-000000000003', 'INV-2024-0003', 'dddddddd-0000-0000-0000-000000000003', 'cccccccc-0000-0000-0000-000000000002', 3000.00,600.00, 3600.00, '2024-02-28', 'PAID',    false, NOW(), NOW()),
  ('44444444-0000-0000-0000-000000000004', 'INV-2024-0004', 'dddddddd-0000-0000-0000-000000000004', 'cccccccc-0000-0000-0000-000000000003', 400.00, 80.00,  480.00,  '2024-04-30', 'PENDING', false, NOW(), NOW()),
  ('44444444-0000-0000-0000-000000000005', 'INV-2024-0005', 'dddddddd-0000-0000-0000-000000000005', 'cccccccc-0000-0000-0000-000000000004', 500.00, 100.00, 600.00,  '2024-05-31', 'PENDING', false, NOW(), NOW()),
  ('44444444-0000-0000-0000-000000000006', 'INV-2024-0006', 'dddddddd-0000-0000-0000-000000000001', 'cccccccc-0000-0000-0000-000000000001', 500.00, 100.00, 600.00,  '2024-07-31', 'EXPIRED', false, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- Payments: method (enum: CREDIT_CARD, BANK_TRANSFER, etc), NO policyId column
INSERT INTO payments (id, invoice_id, client_id, amount, method, status, transaction_id, created_at) VALUES
  ('55555555-0000-0000-0000-000000000001', '44444444-0000-0000-0000-000000000001', 'cccccccc-0000-0000-0000-000000000001', 600.00,  'CREDIT_CARD',   'COMPLETED', 'TXN-20240128-001', NOW()),
  ('55555555-0000-0000-0000-000000000002', '44444444-0000-0000-0000-000000000002', 'cccccccc-0000-0000-0000-000000000001', 840.00,  'BANK_TRANSFER', 'COMPLETED', 'TXN-20240328-002', NOW()),
  ('55555555-0000-0000-0000-000000000003', '44444444-0000-0000-0000-000000000003', 'cccccccc-0000-0000-0000-000000000002', 3600.00, 'BANK_TRANSFER', 'COMPLETED', 'TXN-20240225-003', NOW())
ON CONFLICT (id) DO NOTHING;
