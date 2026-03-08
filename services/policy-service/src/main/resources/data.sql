-- ============================================================
-- Policy Service - Fake Data
-- PolicyEntity.id = String, clientId = String
-- CoverageEntity: coverage_type (String), amount, deductible, percentage
--                 FK via policy_id (JoinColumn)
-- BeneficiaryEntity: name (String), relationship (String), percentage (Double)
--                    FK via policy_id (JoinColumn)
-- ============================================================

-- Policies (id & clientId are String type)
INSERT INTO policies (id, policy_number, client_id, type, status, start_date, end_date, premium_amount, coverage_amount, created_at, updated_at) VALUES
  ('dddddddd-0000-0000-0000-000000000001', 'POL-2024-0001', 'cccccccc-0000-0000-0000-000000000001', 'HEALTH',   'ACTIVE',    '2024-01-01', '2024-12-31', 500.00,  10000.00,  NOW(), NOW()),
  ('dddddddd-0000-0000-0000-000000000002', 'POL-2024-0002', 'cccccccc-0000-0000-0000-000000000001', 'VEHICLE',  'ACTIVE',    '2024-03-01', '2025-03-01', 700.00,  10000.00,  NOW(), NOW()),
  ('dddddddd-0000-0000-0000-000000000003', 'POL-2024-0003', 'cccccccc-0000-0000-0000-000000000002', 'LIFE',     'ACTIVE',    '2024-02-01', '2034-02-01', 3000.00, 100000.00, NOW(), NOW()),
  ('dddddddd-0000-0000-0000-000000000004', 'POL-2024-0004', 'cccccccc-0000-0000-0000-000000000003', 'HOME',     'ACTIVE',    '2024-04-01', '2025-04-01', 400.00,  10000.00,  NOW(), NOW()),
  ('dddddddd-0000-0000-0000-000000000005', 'POL-2024-0005', 'cccccccc-0000-0000-0000-000000000004', 'HEALTH',   'ACTIVE',    '2024-05-01', '2025-05-01', 500.00,  10000.00,  NOW(), NOW()),
  ('dddddddd-0000-0000-0000-000000000006', 'POL-2024-0006', 'cccccccc-0000-0000-0000-000000000004', 'VEHICLE',  'EXPIRED',   '2023-01-01', '2024-01-01', 700.00,  10000.00,  NOW(), NOW()),
  ('dddddddd-0000-0000-0000-000000000007', 'POL-2024-0007', 'cccccccc-0000-0000-0000-000000000005', 'BUSINESS', 'CANCELLED', '2024-01-01', '2024-06-01', 6000.00, 100000.00, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- Coverages: coverage_type (not "type"), amount (not limit_amount), policy_id FK
INSERT INTO coverages (id, policy_id, coverage_type, amount) VALUES
  ('22222222-0000-0000-0000-000000000001', 'dddddddd-0000-0000-0000-000000000001', 'Frais d hospitalisation', 5000.00),
  ('22222222-0000-0000-0000-000000000002', 'dddddddd-0000-0000-0000-000000000001', 'Consultations medicales',  2000.00),
  ('22222222-0000-0000-0000-000000000003', 'dddddddd-0000-0000-0000-000000000002', 'Dommages collision',       8000.00),
  ('22222222-0000-0000-0000-000000000004', 'dddddddd-0000-0000-0000-000000000003', 'Capital deces',           100000.00),
  ('22222222-0000-0000-0000-000000000005', 'dddddddd-0000-0000-0000-000000000004', 'Incendie et degats',       8000.00)
ON CONFLICT (id) DO NOTHING;

-- Beneficiaries: name (not first_name/last_name), percentage (not share_percentage)
INSERT INTO beneficiaries (id, policy_id, name, relationship, percentage) VALUES
  ('33333333-0000-0000-0000-000000000001', 'dddddddd-0000-0000-0000-000000000003', 'Karim Benali',   'EPOUX',  50.00),
  ('33333333-0000-0000-0000-000000000002', 'dddddddd-0000-0000-0000-000000000003', 'Yasmine Benali', 'ENFANT', 50.00)
ON CONFLICT (id) DO NOTHING;
