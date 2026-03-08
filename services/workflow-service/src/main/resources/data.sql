-- ============================================================
-- Workflow Service - Fake Data
-- SAGATransactionEntity: UUID id/initiatedBy, sagaType String, SAGAStatus enum
-- SAGAStepEntity: saga_transaction_id FK, serviceName, action, StepStatus enum,
--                 compensationAction, startedAt, completedAt, errorDetails
-- StepStatus enum: PENDING, IN_PROGRESS, COMPLETED, FAILED, COMPENSATING, COMPENSATED
-- ============================================================

-- SAGA Transactions
INSERT INTO saga_transactions (id, saga_type, status, initiated_by, created_at, updated_at) VALUES
  ('99999999-0000-0000-0000-000000000001', 'POLICY_CREATION_SAGA', 'COMPLETED',   'cccccccc-0000-0000-0000-000000000001', NOW(), NOW()),
  ('99999999-0000-0000-0000-000000000002', 'POLICY_CREATION_SAGA', 'COMPLETED',   'cccccccc-0000-0000-0000-000000000002', NOW(), NOW()),
  ('99999999-0000-0000-0000-000000000003', 'CLAIM_PROCESSING_SAGA','COMPLETED',   'cccccccc-0000-0000-0000-000000000001', NOW(), NOW()),
  ('99999999-0000-0000-0000-000000000004', 'CLAIM_PROCESSING_SAGA','IN_PROGRESS', 'cccccccc-0000-0000-0000-000000000003', NOW(), NOW()),
  ('99999999-0000-0000-0000-000000000005', 'POLICY_CREATION_SAGA', 'FAILED',      'cccccccc-0000-0000-0000-000000000005', NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- SAGA Steps: service_name + action (not step_name)
INSERT INTO saga_steps (id, saga_transaction_id, service_name, action, status, compensation_action, started_at, completed_at) VALUES
  -- SAGA 1 steps (POLICY_CREATION - COMPLETED)
  ('1a1a1a1a-0000-0000-0000-000000000001', '99999999-0000-0000-0000-000000000001', 'client-service',       'VALIDATE_CLIENT',   'COMPLETED', 'ROLLBACK_VALIDATE_CLIENT', NOW(), NOW()),
  ('1a1a1a1a-0000-0000-0000-000000000002', '99999999-0000-0000-0000-000000000001', 'policy-service',       'CREATE_POLICY',     'COMPLETED', 'DELETE_POLICY',            NOW(), NOW()),
  ('1a1a1a1a-0000-0000-0000-000000000003', '99999999-0000-0000-0000-000000000001', 'billing-service',      'GENERATE_INVOICE',  'COMPLETED', 'CANCEL_INVOICE',           NOW(), NOW()),
  ('1a1a1a1a-0000-0000-0000-000000000004', '99999999-0000-0000-0000-000000000001', 'notification-service', 'SEND_NOTIFICATION', 'COMPLETED', NULL,                       NOW(), NOW()),
  -- SAGA 3 steps (CLAIM_PROCESSING - COMPLETED)
  ('1a1a1a1a-0000-0000-0000-000000000005', '99999999-0000-0000-0000-000000000003', 'claims-service',       'VALIDATE_CLAIM',    'COMPLETED', 'ROLLBACK_VALIDATE_CLAIM',  NOW(), NOW()),
  ('1a1a1a1a-0000-0000-0000-000000000006', '99999999-0000-0000-0000-000000000003', 'claims-service',       'ASSESS_CLAIM',      'COMPLETED', 'ROLLBACK_ASSESS_CLAIM',    NOW(), NOW()),
  ('1a1a1a1a-0000-0000-0000-000000000007', '99999999-0000-0000-0000-000000000003', 'billing-service',      'PROCESS_PAYOUT',    'COMPLETED', 'REFUND_PAYOUT',            NOW(), NOW()),
  ('1a1a1a1a-0000-0000-0000-000000000008', '99999999-0000-0000-0000-000000000003', 'notification-service', 'NOTIFY_CLIENT',     'COMPLETED', NULL,                       NOW(), NOW()),
  -- SAGA 4 steps (CLAIM_PROCESSING - IN_PROGRESS)
  ('1a1a1a1a-0000-0000-0000-000000000009', '99999999-0000-0000-0000-000000000004', 'claims-service',       'VALIDATE_CLAIM',    'COMPLETED', 'ROLLBACK_VALIDATE_CLAIM',  NOW(), NOW()),
  ('1a1a1a1a-0000-0000-0000-000000000010', '99999999-0000-0000-0000-000000000004', 'claims-service',       'ASSESS_CLAIM',      'PENDING',   'ROLLBACK_ASSESS_CLAIM',    NULL,  NULL)
ON CONFLICT (id) DO NOTHING;
