-- ============================================================
-- Billing Service - Seed Data
-- ============================================================
-- Note: Foreign keys are handled by Hibernate
-- This data is inserted after schema creation
-- ============================================================

-- Invoices (policy_id and client_id are just UUIDs, no FK constraint in DB)
INSERT INTO invoices (
  id, invoice_number, policy_id, client_id,
  amount, tax_amount, total_amount,
  due_date, status,
  generated_by, paid_direct,
  created_at, updated_at
) VALUES
  ('30000000-0000-0000-0000-000000000001', 'INV-2026-001',
   '40000000-0000-0000-0000-000000000001', 'cccccccc-0000-0000-0000-000000000001',
   1200.00, 120.00, 1320.00,
   CURRENT_DATE + INTERVAL '15 days', 'PENDING',
   '20000000-0000-0000-0000-000000000002', false,
   NOW(), NOW()),
  
  ('30000000-0000-0000-0000-000000000002', 'INV-2026-002',
   '40000000-0000-0000-0000-000000000002', 'cccccccc-0000-0000-0000-000000000002',
   800.00, 80.00, 880.00,
   CURRENT_DATE + INTERVAL '20 days', 'PENDING',
   '20000000-0000-0000-0000-000000000002', false,
   NOW(), NOW()),
  
  ('30000000-0000-0000-0000-000000000003', 'INV-2026-003',
   '40000000-0000-0000-0000-000000000003', 'cccccccc-0000-0000-0000-000000000003',
   2400.00, 240.00, 2640.00,
   CURRENT_DATE + INTERVAL '25 days', 'PENDING',
   '20000000-0000-0000-0000-000000000002', false,
   NOW(), NOW()),
  
  ('30000000-0000-0000-0000-000000000004', 'INV-2026-004',
   '40000000-0000-0000-0000-000000000004', 'cccccccc-0000-0000-0000-000000000004',
   1800.00, 180.00, 1980.00,
   CURRENT_DATE + INTERVAL '30 days', 'PAID',
   '20000000-0000-0000-0000-000000000002', true,
   NOW(), NOW()),
  
  ('30000000-0000-0000-0000-000000000005', 'INV-2026-005',
   '40000000-0000-0000-0000-000000000005', 'cccccccc-0000-0000-0000-000000000005',
   600.00, 60.00, 660.00,
   CURRENT_DATE + INTERVAL '10 days', 'PENDING',
   '20000000-0000-0000-0000-000000000002', false,
   NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- Payments
INSERT INTO payments (
  id, payment_number, invoice_id,
  amount, payment_date, payment_method, status,
  created_at, updated_at
) VALUES
  ('31000000-0000-0000-0000-000000000001', 'PAY-2026-001',
   '30000000-0000-0000-0000-000000000001',
   1320.00, CURRENT_DATE - INTERVAL '5 days', 'CREDIT_CARD', 'COMPLETED',
   NOW(), NOW()),
  
  ('31000000-0000-0000-0000-000000000002', 'PAY-2026-002',
   '30000000-0000-0000-0000-000000000002',
   880.00, CURRENT_DATE - INTERVAL '3 days', 'BANK_TRANSFER', 'COMPLETED',
   NOW(), NOW()),
  
  ('31000000-0000-0000-0000-000000000003', 'PAY-2026-003',
   '30000000-0000-0000-0000-000000000003',
   1320.00, CURRENT_DATE - INTERVAL '2 days', 'CREDIT_CARD', 'PENDING',
   NOW(), NOW()),
  
  ('31000000-0000-0000-0000-000000000004', 'PAY-2026-004',
   '30000000-0000-0000-0000-000000000004',
   990.00, CURRENT_DATE - INTERVAL '8 days', 'CASH', 'COMPLETED',
   NOW(), NOW()),
  
  ('31000000-0000-0000-0000-000000000005', 'PAY-2026-005',
   '30000000-0000-0000-0000-000000000005',
   330.00, CURRENT_DATE - INTERVAL '10 days', 'BANK_TRANSFER', 'COMPLETED',
   NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
