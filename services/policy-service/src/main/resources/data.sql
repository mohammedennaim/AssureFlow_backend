-- ============================================================
-- Policy Service - Comprehensive Seed Data for Testing
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
  -- Active Vehicle Policies
  (
    '50000000-0000-0000-0000-000000000001',
    'POL-VEH-2026-001',
    'cccccccc-0000-0000-0000-000000000001',
    'VEHICLE',
    'ACTIVE',
    CURRENT_DATE - INTERVAL '30 days',
    CURRENT_DATE + INTERVAL '335 days',
    1200.00,
    50000.00,
    NOW(),
    NOW()
  ),
  (
    '50000000-0000-0000-0000-000000000002',
    'POL-VEH-2026-002',
    'cccccccc-0000-0000-0000-000000000002',
    'VEHICLE',
    'ACTIVE',
    CURRENT_DATE - INTERVAL '60 days',
    CURRENT_DATE + INTERVAL '305 days',
    1500.00,
    75000.00,
    NOW(),
    NOW()
  ),
  (
    '50000000-0000-0000-0000-000000000003',
    'POL-VEH-2026-003',
    'cccccccc-0000-0000-0000-000000000007',
    'VEHICLE',
    'ACTIVE',
    CURRENT_DATE - INTERVAL '15 days',
    CURRENT_DATE + INTERVAL '350 days',
    1800.00,
    100000.00,
    NOW(),
    NOW()
  ),
  
  -- Active Home Policies
  (
    '50000000-0000-0000-0000-000000000004',
    'POL-HOME-2026-001',
    'cccccccc-0000-0000-0000-000000000003',
    'HOME',
    'ACTIVE',
    CURRENT_DATE - INTERVAL '90 days',
    CURRENT_DATE + INTERVAL '275 days',
    2500.00,
    200000.00,
    NOW(),
    NOW()
  ),
  (
    '50000000-0000-0000-0000-000000000005',
    'POL-HOME-2026-002',
    'cccccccc-0000-0000-0000-000000000004',
    'HOME',
    'ACTIVE',
    CURRENT_DATE - INTERVAL '45 days',
    CURRENT_DATE + INTERVAL '320 days',
    3000.00,
    300000.00,
    NOW(),
    NOW()
  ),
  (
    '50000000-0000-0000-0000-000000000006',
    'POL-HOME-2026-003',
    'cccccccc-0000-0000-0000-000000000006',
    'HOME',
    'ACTIVE',
    CURRENT_DATE - INTERVAL '120 days',
    CURRENT_DATE + INTERVAL '245 days',
    2200.00,
    180000.00,
    NOW(),
    NOW()
  ),
  
  -- Active Life Policies
  (
    '50000000-0000-0000-0000-000000000007',
    'POL-LIFE-2026-001',
    'cccccccc-0000-0000-0000-000000000001',
    'LIFE',
    'ACTIVE',
    CURRENT_DATE - INTERVAL '180 days',
    CURRENT_DATE + INTERVAL '185 days',
    5000.00,
    500000.00,
    NOW(),
    NOW()
  ),
  (
    '50000000-0000-0000-0000-000000000008',
    'POL-LIFE-2026-002',
    'cccccccc-0000-0000-0000-000000000008',
    'LIFE',
    'ACTIVE',
    CURRENT_DATE - INTERVAL '200 days',
    CURRENT_DATE + INTERVAL '165 days',
    6000.00,
    750000.00,
    NOW(),
    NOW()
  ),
  
  -- Active Health Policies
  (
    '50000000-0000-0000-0000-000000000009',
    'POL-HEALTH-2026-001',
    'cccccccc-0000-0000-0000-000000000002',
    'HEALTH',
    'ACTIVE',
    CURRENT_DATE - INTERVAL '100 days',
    CURRENT_DATE + INTERVAL '265 days',
    3500.00,
    100000.00,
    NOW(),
    NOW()
  ),
  (
    '50000000-0000-0000-0000-000000000010',
    'POL-HEALTH-2026-002',
    'cccccccc-0000-0000-0000-000000000010',
    'HEALTH',
    'ACTIVE',
    CURRENT_DATE - INTERVAL '50 days',
    CURRENT_DATE + INTERVAL '315 days',
    4000.00,
    150000.00,
    NOW(),
    NOW()
  ),
  
  -- Active Business Policies
  (
    '50000000-0000-0000-0000-000000000011',
    'POL-BUS-2026-001',
    'cccccccc-0000-0000-0000-000000000011',
    'BUSINESS',
    'ACTIVE',
    CURRENT_DATE - INTERVAL '150 days',
    CURRENT_DATE + INTERVAL '215 days',
    15000.00,
    1000000.00,
    NOW(),
    NOW()
  ),
  (
    '50000000-0000-0000-0000-000000000012',
    'POL-BUS-2026-002',
    'cccccccc-0000-0000-0000-000000000012',
    'BUSINESS',
    'ACTIVE',
    CURRENT_DATE - INTERVAL '80 days',
    CURRENT_DATE + INTERVAL '285 days',
    20000.00,
    1500000.00,
    NOW(),
    NOW()
  ),
  
  -- Pending Policies (awaiting approval)
  (
    '50000000-0000-0000-0000-000000000013',
    'POL-VEH-2026-004',
    'cccccccc-0000-0000-0000-000000000009',
    'VEHICLE',
    'PENDING',
    CURRENT_DATE + INTERVAL '5 days',
    CURRENT_DATE + INTERVAL '370 days',
    1400.00,
    60000.00,
    NOW(),
    NOW()
  ),
  (
    '50000000-0000-0000-0000-000000000014',
    'POL-HOME-2026-004',
    'cccccccc-0000-0000-0000-000000000010',
    'HOME',
    'PENDING',
    CURRENT_DATE + INTERVAL '10 days',
    CURRENT_DATE + INTERVAL '375 days',
    2800.00,
    250000.00,
    NOW(),
    NOW()
  ),
  
  -- Draft Policies (not yet submitted)
  (
    '50000000-0000-0000-0000-000000000015',
    'POL-HEALTH-2026-003',
    'cccccccc-0000-0000-0000-000000000006',
    'HEALTH',
    'DRAFT',
    CURRENT_DATE + INTERVAL '15 days',
    CURRENT_DATE + INTERVAL '380 days',
    3800.00,
    120000.00,
    NOW(),
    NOW()
  ),
  
  -- Expired Policies
  (
    '50000000-0000-0000-0000-000000000016',
    'POL-VEH-2025-001',
    'cccccccc-0000-0000-0000-000000000003',
    'VEHICLE',
    'EXPIRED',
    CURRENT_DATE - INTERVAL '400 days',
    CURRENT_DATE - INTERVAL '35 days',
    1100.00,
    45000.00,
    NOW(),
    NOW()
  ),
  (
    '50000000-0000-0000-0000-000000000017',
    'POL-HOME-2025-001',
    'cccccccc-0000-0000-0000-000000000004',
    'HOME',
    'EXPIRED',
    CURRENT_DATE - INTERVAL '450 days',
    CURRENT_DATE - INTERVAL '85 days',
    2400.00,
    190000.00,
    NOW(),
    NOW()
  ),
  
  -- Cancelled Policies
  (
    '50000000-0000-0000-0000-000000000018',
    'POL-VEH-2025-002',
    'cccccccc-0000-0000-0000-000000000005',
    'VEHICLE',
    'CANCELLED',
    CURRENT_DATE - INTERVAL '200 days',
    CURRENT_DATE + INTERVAL '165 days',
    1300.00,
    55000.00,
    NOW(),
    NOW()
  ),
  (
    '50000000-0000-0000-0000-000000000019',
    'POL-LIFE-2025-001',
    'cccccccc-0000-0000-0000-000000000014',
    'LIFE',
    'CANCELLED',
    CURRENT_DATE - INTERVAL '250 days',
    CURRENT_DATE + INTERVAL '115 days',
    5500.00,
    600000.00,
    NOW(),
    NOW()
  ),
  
  -- Suspended Policy
  (
    '50000000-0000-0000-0000-000000000020',
    'POL-BUS-2025-001',
    'cccccccc-0000-0000-0000-000000000013',
    'BUSINESS',
    'SUSPENDED',
    CURRENT_DATE - INTERVAL '180 days',
    CURRENT_DATE + INTERVAL '185 days',
    18000.00,
    1200000.00,
    NOW(),
    NOW()
  )
ON CONFLICT (id) DO NOTHING;