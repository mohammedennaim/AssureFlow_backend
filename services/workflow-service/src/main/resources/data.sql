-- ============================================================
-- Workflow Service - Seed data aligned with current entities
-- ============================================================

INSERT INTO saga_transactions (id, saga_type, status, initiated_by, created_at, updated_at) VALUES
  (
    '70000000-0000-0000-0000-000000000001',
    'POLICY_CREATION',
    'IN_PROGRESS',
    '20000000-0000-0000-0000-000000000002',
    NOW(),
    NOW()
  )
ON CONFLICT (id) DO NOTHING;

INSERT INTO saga_steps (
  id,
  saga_transaction_id,
  service_name,
  action,
  status,
  compensation_action,
  started_at,
  completed_at,
  error_details
) VALUES
  (
    '71000000-0000-0000-0000-000000000001',
    '70000000-0000-0000-0000-000000000001',
    'policy-service',
    'CREATE_POLICY',
    'COMPLETED',
    NULL,
    NOW() - INTERVAL '5 minutes',
    NOW() - INTERVAL '4 minutes',
    NULL
  ),
  (
    '71000000-0000-0000-0000-000000000002',
    '70000000-0000-0000-0000-000000000001',
    'billing-service',
    'GENERATE_INVOICE',
    'IN_PROGRESS',
    'CANCEL_INVOICE',
    NOW() - INTERVAL '2 minutes',
    NULL,
    NULL
  )
ON CONFLICT (id) DO NOTHING;