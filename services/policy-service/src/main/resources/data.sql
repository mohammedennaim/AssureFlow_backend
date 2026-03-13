-- ============================================================
-- Policy Service - Fake Data
-- Policies with different types and statuses
-- ============================================================

-- Insert Policies
INSERT INTO policies (id, policy_number, client_id, type, status, premium_amount, coverage_amount, start_date, end_date, created_at, updated_at, created_by) VALUES
  ('pppppppp-0000-0000-0000-000000000001', 'POL-AUTO-001', 'cccccccc-0000-0000-0000-000000000001', 'AUTO',   'ACTIVE',    1200.00, 50000.00, '2024-01-01', '2024-12-31', NOW() - INTERVAL '30 days', NOW(), 'aaaaaaaa-0000-0000-0000-000000000002'),
  ('pppppppp-0000-0000-0000-000000000002', 'POL-HOME-002', 'cccccccc-0000-0000-0000-000000000001', 'HOME',   'ACTIVE',    800.00,  100000.00, '2024-02-01', '2025-01-31', NOW() - INTERVAL '25 days', NOW(), 'aaaaaaaa-0000-0000-0000-000000000002'),
  ('pppppppp-0000-0000-0000-000000000003', 'POL-LIFE-003', 'cccccccc-0000-0000-0000-000000000002', 'LIFE',   'ACTIVE',    2400.00, 200000.00, '2024-01-15', '2025-01-14', NOW() - INTERVAL '20 days', NOW(), 'aaaaaaaa-0000-0000-0000-000000000003'),
  ('pppppppp-0000-0000-0000-000000000004', 'POL-HEALTH-004', 'cccccccc-0000-0000-0000-000000000002', 'HEALTH', 'ACTIVE',    1800.00, 75000.00, '2024-03-01', '2025-02-28', NOW() - INTERVAL '15 days', NOW(), 'aaaaaaaa-0000-0000-0000-000000000003'),
  ('pppppppp-0000-0000-0000-000000000005', 'POL-AUTO-005', 'cccccccc-0000-0000-0000-000000000003', 'AUTO',   'SUSPENDED', 1500.00, 60000.00, '2024-01-01', '2024-12-31', NOW() - INTERVAL '10 days', NOW(), 'aaaaaaaa-0000-0000-0000-000000000002'),
  ('pppppppp-0000-0000-0000-000000000006', 'POL-TRAVEL-006', 'cccccccc-0000-0000-0000-000000000004', 'TRAVEL', 'DRAFT',     300.00,  25000.00, '2024-04-01', '2024-04-30', NOW() - INTERVAL '5 days', NOW(), 'aaaaaaaa-0000-0000-0000-000000000002'),
  ('pppppppp-0000-0000-0000-000000000007', 'POL-HOME-007', 'cccccccc-0000-0000-0000-000000000005', 'HOME',   'EXPIRED',   900.00,  120000.00, '2023-01-01', '2023-12-31', NOW() - INTERVAL '60 days', NOW(), 'aaaaaaaa-0000-0000-0000-000000000003')
ON CONFLICT (id) DO NOTHING;

-- Insert Policy Details (additional coverage information)
INSERT INTO policy_details (id, policy_id, coverage_type, coverage_limit, deductible, description) VALUES
  ('dddddddd-1000-0000-0000-000000000001', 'pppppppp-0000-0000-0000-000000000001', 'COLLISION',     25000.00, 500.00, 'Collision damage coverage'),
  ('dddddddd-1000-0000-0000-000000000002', 'pppppppp-0000-0000-0000-000000000001', 'COMPREHENSIVE', 25000.00, 300.00, 'Comprehensive coverage including theft'),
  ('dddddddd-1000-0000-0000-000000000003', 'pppppppp-0000-0000-0000-000000000002', 'FIRE',          50000.00, 1000.00, 'Fire damage coverage'),
  ('dddddddd-1000-0000-0000-000000000004', 'pppppppp-0000-0000-0000-000000000002', 'THEFT',         50000.00, 500.00, 'Theft protection coverage'),
  ('dddddddd-1000-0000-0000-000000000005', 'pppppppp-0000-0000-0000-000000000003', 'DEATH',         200000.00, 0.00, 'Death benefit coverage'),
  ('dddddddd-1000-0000-0000-000000000006', 'pppppppp-0000-0000-0000-000000000004', 'MEDICAL',       75000.00, 200.00, 'Medical expenses coverage')
ON CONFLICT (id) DO NOTHING;

-- Insert Policy Events (for audit trail)
INSERT INTO policy_events (id, policy_id, event_type, event_data, created_at, created_by) VALUES
  ('eeeeeeee-0000-0000-0000-000000000001', 'pppppppp-0000-0000-0000-000000000001', 'POLICY_CREATED', '{"premium": 1200.00, "coverage": 50000.00}', NOW() - INTERVAL '30 days', 'aaaaaaaa-0000-0000-0000-000000000002'),
  ('eeeeeeee-0000-0000-0000-000000000002', 'pppppppp-0000-0000-0000-000000000001', 'POLICY_ACTIVATED', '{"activation_date": "2024-01-01"}', NOW() - INTERVAL '29 days', 'aaaaaaaa-0000-0000-0000-000000000002'),
  ('eeeeeeee-0000-0000-0000-000000000003', 'pppppppp-0000-0000-0000-000000000005', 'POLICY_SUSPENDED', '{"reason": "Non-payment", "suspension_date": "2024-03-01"}', NOW() - INTERVAL '5 days', 'aaaaaaaa-0000-0000-0000-000000000002'),
  ('eeeeeeee-0000-0000-0000-000000000004', 'pppppppp-0000-0000-0000-000000000007', 'POLICY_EXPIRED', '{"expiry_date": "2023-12-31"}', NOW() - INTERVAL '60 days', 'SYSTEM')
ON CONFLICT (id) DO NOTHING;