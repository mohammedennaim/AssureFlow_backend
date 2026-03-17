-- ============================================================
-- Billing Service - Seed data aligned with current entities
-- ============================================================

INSERT INTO invoices (
  id,
  invoice_number,
  policy_id,
  client_id,
  amount,
  tax_amount,
  total_amount,
  due_date,
  status,
  generated_by,
  paid_direct,
  created_at,
  updated_at
) VALUES
  (
    '30000000-0000-0000-0000-000000000001',
    'INV-2026-001',
    '40000000-0000-0000-0000-000000000001',
    '50000000-0000-0000-0000-000000000001',
    1200.00,
    120.00,
    1320.00,
    CURRENT_DATE + INTERVAL '15 days',
    'PENDING',
    '20000000-0000-0000-0000-000000000002',
    false,
    NOW(),
    NOW()
  )
ON CONFLICT (id) DO NOTHING;