-- ============================================================
-- Policy Service - Seed data aligned with current entities
-- ============================================================

INSERT INTO policies (
  id,
  policy_number,
  client_id,
  type,
  status,
  start_date,
  end_date,
  premium_amount,
  coverage_amount,
  created_at,
  updated_at
) VALUES
  (
    '40000000-0000-0000-0000-000000000001',
    'POL-VEHICLE-2026-001',
    '50000000-0000-0000-0000-000000000001',
    'VEHICLE',
    'ACTIVE',
    CURRENT_DATE - INTERVAL '10 days',
    CURRENT_DATE + INTERVAL '355 days',
    1200.00,
    50000.00,
    NOW(),
    NOW()
  )
ON CONFLICT (id) DO NOTHING;