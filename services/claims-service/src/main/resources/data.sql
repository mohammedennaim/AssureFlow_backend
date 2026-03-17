-- ============================================================
-- Claims Service - Seed data aligned with current entities
-- ============================================================

INSERT INTO claims (
  id,
  claim_number,
  policy_id,
  client_id,
  status,
  incident_date,
  description,
  estimated_amount,
  approved_amount,
  submitted_by,
  approved_by,
  assigned_to,
  created_at,
  updated_at
) VALUES
  (
    '60000000-0000-0000-0000-000000000001',
    'CLM-2026-001',
    '40000000-0000-0000-0000-000000000001',
    '50000000-0000-0000-0000-000000000001',
    'UNDER_REVIEW',
    CURRENT_DATE - INTERVAL '2 days',
    'Claim created for docker smoke testing',
    2500.00,
    NULL,
    '20000000-0000-0000-0000-000000000003',
    NULL,
    '20000000-0000-0000-0000-000000000002',
    NOW(),
    NOW()
  )
ON CONFLICT (id) DO NOTHING;