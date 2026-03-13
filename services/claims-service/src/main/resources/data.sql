-- ============================================================
-- Claims Service - Fake Data
-- Claims with different statuses and SLA tracking
-- ============================================================

-- Insert Claims
INSERT INTO claims (id, claim_number, policy_id, client_id, type, status, amount_claimed, amount_approved, incident_date, submitted_date, description, created_at, updated_at, assigned_to, sla_deadline) VALUES
  ('xxxxxxxx-0000-0000-0000-000000000001', 'CLM-2024-001', 'pppppppp-0000-0000-0000-000000000001', 'cccccccc-0000-0000-0000-000000000001', 'AUTO_ACCIDENT', 'PAID',         8500.00, 8000.00, '2024-02-15', '2024-02-16', 'Collision with another vehicle at intersection', NOW() - INTERVAL '20 days', NOW() - INTERVAL '5 days', 'aaaaaaaa-0000-0000-0000-000000000007', NOW() - INTERVAL '18 days'),
  ('xxxxxxxx-0000-0000-0000-000000000002', 'CLM-2024-002', 'pppppppp-0000-0000-0000-000000000002', 'cccccccc-0000-0000-0000-000000000001', 'FIRE_DAMAGE', 'APPROVED',     15000.00, 12000.00, '2024-02-20', '2024-02-21', 'Kitchen fire caused damage to property', NOW() - INTERVAL '15 days', NOW() - INTERVAL '3 days', 'aaaaaaaa-0000-0000-0000-000000000007', NOW() + INTERVAL '1 day'),
  ('xxxxxxxx-0000-0000-0000-000000000003', 'CLM-2024-003', 'pppppppp-0000-0000-0000-000000000003', 'cccccccc-0000-0000-0000-000000000002', 'MEDICAL',      'UNDER_REVIEW', 3500.00, NULL,    '2024-03-01', '2024-03-02', 'Emergency surgery following accident', NOW() - INTERVAL '10 days', NOW() - INTERVAL '2 days', 'aaaaaaaa-0000-0000-0000-000000000007', NOW() + INTERVAL '1 day'),
  ('xxxxxxxx-0000-0000-0000-000000000004', 'CLM-2024-004', 'pppppppp-0000-0000-0000-000000000004', 'cccccccc-0000-0000-0000-000000000002', 'DENTAL',       'SUBMITTED',    800.00,  NULL,    '2024-03-05', '2024-03-06', 'Dental treatment after sports injury', NOW() - INTERVAL '5 days', NOW() - INTERVAL '5 days', NULL, NOW() + INTERVAL '2 days'),
  ('xxxxxxxx-0000-0000-0000-000000000005', 'CLM-2024-005', 'pppppppp-0000-0000-0000-000000000001', 'cccccccc-0000-0000-0000-000000000001', 'THEFT',        'REJECTED',     2500.00, 0.00,    '2024-02-28', '2024-03-01', 'Vehicle theft claim - insufficient evidence', NOW() - INTERVAL '8 days', NOW() - INTERVAL '1 day', 'aaaaaaaa-0000-0000-0000-000000000007', NOW() - INTERVAL '6 days'),
  ('xxxxxxxx-0000-0000-0000-000000000006', 'CLM-2024-006', 'pppppppp-0000-0000-0000-000000000002', 'cccccccc-0000-0000-0000-000000000001', 'WATER_DAMAGE', 'PAYOUT_INITIATED', 5500.00, 5000.00, '2024-03-03', '2024-03-04', 'Pipe burst caused flooding in basement', NOW() - INTERVAL '6 days', NOW() - INTERVAL '1 day', 'aaaaaaaa-0000-0000-0000-000000000007', NOW() + INTERVAL '1 day')
ON CONFLICT (id) DO NOTHING;

-- Insert Claim Documents
INSERT INTO claim_documents (id, claim_id, document_type, file_name, file_path, uploaded_date, uploaded_by) VALUES
  ('ffffffff-0000-0000-0000-000000000001', 'xxxxxxxx-0000-0000-0000-000000000001', 'POLICE_REPORT', 'police_report_001.pdf', '/documents/claims/CLM-2024-001/police_report_001.pdf', NOW() - INTERVAL '19 days', 'cccccccc-0000-0000-0000-000000000001'),
  ('ffffffff-0000-0000-0000-000000000002', 'xxxxxxxx-0000-0000-0000-000000000001', 'PHOTOS', 'accident_photos.zip', '/documents/claims/CLM-2024-001/accident_photos.zip', NOW() - INTERVAL '19 days', 'cccccccc-0000-0000-0000-000000000001'),
  ('ffffffff-0000-0000-0000-000000000003', 'xxxxxxxx-0000-0000-0000-000000000002', 'FIRE_REPORT', 'fire_department_report.pdf', '/documents/claims/CLM-2024-002/fire_department_report.pdf', NOW() - INTERVAL '14 days', 'cccccccc-0000-0000-0000-000000000001'),
  ('ffffffff-0000-0000-0000-000000000004', 'xxxxxxxx-0000-0000-0000-000000000003', 'MEDICAL_REPORT', 'medical_report_surgery.pdf', '/documents/claims/CLM-2024-003/medical_report_surgery.pdf', NOW() - INTERVAL '9 days', 'cccccccc-0000-0000-0000-000000000002'),
  ('ffffffff-0000-0000-0000-000000000005', 'xxxxxxxx-0000-0000-0000-000000000004', 'DENTAL_XRAY', 'dental_xray.jpg', '/documents/claims/CLM-2024-004/dental_xray.jpg', NOW() - INTERVAL '4 days', 'cccccccc-0000-0000-0000-000000000002')
ON CONFLICT (id) DO NOTHING;

-- Insert Claim Status History
INSERT INTO claim_status_history (id, claim_id, previous_status, new_status, changed_date, changed_by, reason) VALUES
  ('hhhhhhhh-0000-0000-0000-000000000001', 'xxxxxxxx-0000-0000-0000-000000000001', 'SUBMITTED', 'UNDER_REVIEW', NOW() - INTERVAL '18 days', 'aaaaaaaa-0000-0000-0000-000000000007', 'Initial review started'),
  ('hhhhhhhh-0000-0000-0000-000000000002', 'xxxxxxxx-0000-0000-0000-000000000001', 'UNDER_REVIEW', 'APPROVED', NOW() - INTERVAL '10 days', 'aaaaaaaa-0000-0000-0000-000000000007', 'Documentation verified, claim approved'),
  ('hhhhhhhh-0000-0000-0000-000000000003', 'xxxxxxxx-0000-0000-0000-000000000001', 'APPROVED', 'PAYOUT_INITIATED', NOW() - INTERVAL '7 days', 'aaaaaaaa-0000-0000-0000-000000000006', 'Payment processing initiated'),
  ('hhhhhhhh-0000-0000-0000-000000000004', 'xxxxxxxx-0000-0000-0000-000000000001', 'PAYOUT_INITIATED', 'PAID', NOW() - INTERVAL '5 days', 'SYSTEM', 'Payment completed successfully'),
  ('hhhhhhhh-0000-0000-0000-000000000005', 'xxxxxxxx-0000-0000-0000-000000000005', 'SUBMITTED', 'UNDER_REVIEW', NOW() - INTERVAL '6 days', 'aaaaaaaa-0000-0000-0000-000000000007', 'Review started'),
  ('hhhhhhhh-0000-0000-0000-000000000006', 'xxxxxxxx-0000-0000-0000-000000000005', 'UNDER_REVIEW', 'REJECTED', NOW() - INTERVAL '1 day', 'aaaaaaaa-0000-0000-0000-000000000007', 'Insufficient evidence provided')
ON CONFLICT (id) DO NOTHING;

-- Insert SLA Violations (for monitoring)
INSERT INTO sla_violations (id, claim_id, violation_type, expected_date, actual_date, delay_hours, escalated, escalated_to, created_at) VALUES
  ('ssssssss-0000-0000-0000-000000000001', 'xxxxxxxx-0000-0000-0000-000000000003', 'REVIEW_DEADLINE', NOW() - INTERVAL '1 day', NULL, 24, true, 'aaaaaaaa-0000-0000-0000-000000000001', NOW() - INTERVAL '1 day')
ON CONFLICT (id) DO NOTHING;