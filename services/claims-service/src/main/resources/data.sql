-- ============================================================
-- Claims Service - Fake Data
-- ClaimEntity: UUID id/policyId/clientId, has createdAt/updatedAt
-- ClaimAssessmentEntity: claim_id FK (JoinColumn), assessorId UUID,
--     decision (AssessmentDecision enum: APPROVED, REJECTED, NEEDS_MORE_INFO),
--     amount, status (PrescriptionStatus enum), notes. NO assessed_at column.
-- ============================================================

-- Claims (policyId is UUID type here)
INSERT INTO claims (id, claim_number, policy_id, client_id, status, incident_date, description, estimated_amount, approved_amount, submitted_by, created_at, updated_at) VALUES
  ('66666666-0000-0000-0000-000000000001', 'CLM-2024-0001', 'dddddddd-0000-0000-0000-000000000001', 'cccccccc-0000-0000-0000-000000000001', 'CLOSED',       '2024-02-15', 'Hospitalisation suite a un accident',     3500.00, 3200.00, 'cccccccc-0000-0000-0000-000000000001', NOW(), NOW()),
  ('66666666-0000-0000-0000-000000000002', 'CLM-2024-0002', 'dddddddd-0000-0000-0000-000000000002', 'cccccccc-0000-0000-0000-000000000001', 'PAID',         '2024-04-10', 'Accident de voiture - pare-choc avant',   2800.00, 2500.00, 'cccccccc-0000-0000-0000-000000000001', NOW(), NOW()),
  ('66666666-0000-0000-0000-000000000003', 'CLM-2024-0003', 'dddddddd-0000-0000-0000-000000000004', 'cccccccc-0000-0000-0000-000000000003', 'UNDER_REVIEW', '2024-06-20', 'Degats eau - salle de bain',               5000.00, NULL,    'cccccccc-0000-0000-0000-000000000003', NOW(), NOW()),
  ('66666666-0000-0000-0000-000000000004', 'CLM-2024-0004', 'dddddddd-0000-0000-0000-000000000005', 'cccccccc-0000-0000-0000-000000000004', 'SUBMITTED',    '2024-07-05', 'Frais hospitalisation maternite',          4200.00, NULL,    'cccccccc-0000-0000-0000-000000000004', NOW(), NOW()),
  ('66666666-0000-0000-0000-000000000005', 'CLM-2024-0005', 'dddddddd-0000-0000-0000-000000000001', 'cccccccc-0000-0000-0000-000000000001', 'REJECTED',     '2024-05-03', 'Consultation psychiatre non couverte',     800.00,  NULL,    'cccccccc-0000-0000-0000-000000000001', NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- Claim Assessments: claim_id FK, decision (AssessmentDecision), notes, NO assessed_at
INSERT INTO claim_assessments (id, claim_id, assessor_id, decision, notes) VALUES
  ('77777777-0000-0000-0000-000000000001', '66666666-0000-0000-0000-000000000001', 'aaaaaaaa-0000-0000-0000-000000000002', 'APPROVED', 'Dossier complet, hospitalisation confirmee'),
  ('77777777-0000-0000-0000-000000000002', '66666666-0000-0000-0000-000000000002', 'aaaaaaaa-0000-0000-0000-000000000002', 'APPROVED', 'Photos accident verifiees, devis accepte'),
  ('77777777-0000-0000-0000-000000000003', '66666666-0000-0000-0000-000000000005', 'aaaaaaaa-0000-0000-0000-000000000003', 'REJECTED', 'Consultation non incluse dans la couverture')
ON CONFLICT (id) DO NOTHING;
