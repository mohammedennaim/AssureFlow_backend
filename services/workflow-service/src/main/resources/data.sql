-- ============================================================
-- Workflow Service - Fake Data
-- SAGA Transactions, Steps, and Audit Logs
-- ============================================================

-- Insert SAGA Transactions
INSERT INTO saga_transactions (id, saga_type, status, correlation_id, started_at, completed_at, created_at, updated_at) VALUES
  ('ssssssss-1000-0000-0000-000000000001', 'POLICY_CREATION', 'COMPLETED', 'CORR-POL-001', NOW() - INTERVAL '30 days', NOW() - INTERVAL '29 days', NOW() - INTERVAL '30 days', NOW() - INTERVAL '29 days'),
  ('ssssssss-1000-0000-0000-000000000002', 'POLICY_CREATION', 'COMPLETED', 'CORR-POL-002', NOW() - INTERVAL '25 days', NOW() - INTERVAL '24 days', NOW() - INTERVAL '25 days', NOW() - INTERVAL '24 days'),
  ('ssssssss-1000-0000-0000-000000000003', 'CLAIM_PROCESSING', 'COMPLETED', 'CORR-CLM-001', NOW() - INTERVAL '20 days', NOW() - INTERVAL '5 days', NOW() - INTERVAL '20 days', NOW() - INTERVAL '5 days'),
  ('ssssssss-1000-0000-0000-000000000004', 'CLAIM_PROCESSING', 'IN_PROGRESS', 'CORR-CLM-002', NOW() - INTERVAL '10 days', NULL, NOW() - INTERVAL '10 days', NOW() - INTERVAL '2 days'),
  ('ssssssss-1000-0000-0000-000000000005', 'PAYMENT_PROCESSING', 'FAILED', 'CORR-PAY-001', NOW() - INTERVAL '8 days', NOW() - INTERVAL '7 days', NOW() - INTERVAL '8 days', NOW() - INTERVAL '7 days'),
  ('ssssssss-1000-0000-0000-000000000006', 'POLICY_SUSPENSION', 'COMPENSATING', 'CORR-POL-003', NOW() - INTERVAL '5 days', NULL, NOW() - INTERVAL '5 days', NOW() - INTERVAL '1 day')
ON CONFLICT (id) DO NOTHING;

-- Insert SAGA Steps
INSERT INTO saga_steps (id, saga_transaction_id, step_name, step_order, status, service_name, command_type, request_data, response_data, started_at, completed_at, retry_count, max_retries, created_at, updated_at) VALUES
  -- Policy Creation SAGA (Completed)
  ('tttttttt-1000-0000-0000-000000000001', 'ssssssss-1000-0000-0000-000000000001', 'CREATE_POLICY', 1, 'COMPLETED', 'policy-service', 'CREATE_POLICY_COMMAND', '{"clientId": "cccccccc-0000-0000-0000-000000000001", "type": "AUTO", "premium": 1200.00}', '{"policyId": "pppppppp-0000-0000-0000-000000000001", "status": "CREATED"}', NOW() - INTERVAL '30 days', NOW() - INTERVAL '30 days', 0, 3, NOW() - INTERVAL '30 days', NOW() - INTERVAL '30 days'),
  ('tttttttt-1000-0000-0000-000000000002', 'ssssssss-1000-0000-0000-000000000001', 'GENERATE_INVOICE', 2, 'COMPLETED', 'billing-service', 'GENERATE_INVOICE_COMMAND', '{"policyId": "pppppppp-0000-0000-0000-000000000001", "amount": 1200.00}', '{"invoiceId": "iiiiiiii-0000-0000-0000-000000000001", "status": "GENERATED"}', NOW() - INTERVAL '30 days', NOW() - INTERVAL '29 days', 0, 3, NOW() - INTERVAL '30 days', NOW() - INTERVAL '29 days'),
  ('tttttttt-1000-0000-0000-000000000003', 'ssssssss-1000-0000-0000-000000000001', 'ACTIVATE_POLICY', 3, 'COMPLETED', 'policy-service', 'ACTIVATE_POLICY_COMMAND', '{"policyId": "pppppppp-0000-0000-0000-000000000001"}', '{"policyId": "pppppppp-0000-0000-0000-000000000001", "status": "ACTIVE"}', NOW() - INTERVAL '29 days', NOW() - INTERVAL '29 days', 0, 3, NOW() - INTERVAL '29 days', NOW() - INTERVAL '29 days'),
  
  -- Claim Processing SAGA (In Progress)
  ('tttttttt-1000-0000-0000-000000000004', 'ssssssss-1000-0000-0000-000000000004', 'VALIDATE_CLAIM', 1, 'COMPLETED', 'claims-service', 'VALIDATE_CLAIM_COMMAND', '{"claimId": "xxxxxxxx-0000-0000-0000-000000000003", "policyId": "pppppppp-0000-0000-0000-000000000003"}', '{"claimId": "xxxxxxxx-0000-0000-0000-000000000003", "status": "VALIDATED"}', NOW() - INTERVAL '10 days', NOW() - INTERVAL '9 days', 0, 3, NOW() - INTERVAL '10 days', NOW() - INTERVAL '9 days'),
  ('tttttttt-1000-0000-0000-000000000005', 'ssssssss-1000-0000-0000-000000000004', 'REVIEW_CLAIM', 2, 'EXECUTING', 'claims-service', 'REVIEW_CLAIM_COMMAND', '{"claimId": "xxxxxxxx-0000-0000-0000-000000000003"}', NULL, NOW() - INTERVAL '8 days', NULL, 1, 3, NOW() - INTERVAL '8 days', NOW() - INTERVAL '2 days'),
  
  -- Failed Payment Processing SAGA
  ('tttttttt-1000-0000-0000-000000000006', 'ssssssss-1000-0000-0000-000000000005', 'PROCESS_PAYMENT', 1, 'FAILED', 'billing-service', 'PROCESS_PAYMENT_COMMAND', '{"invoiceId": "iiiiiiii-0000-0000-0000-000000000005", "amount": 1500.00}', '{"error": "Insufficient funds", "code": "PAYMENT_DECLINED"}', NOW() - INTERVAL '8 days', NOW() - INTERVAL '7 days', 3, 3, NOW() - INTERVAL '8 days', NOW() - INTERVAL '7 days')
ON CONFLICT (id) DO NOTHING;

-- Insert Workflow Audit Logs
INSERT INTO workflow_audit_logs (id, saga_transaction_id, event_type, event_data, user_id, service_name, correlation_id, created_at) VALUES
  ('wwwwwwww-0000-0000-0000-000000000001', 'ssssssss-1000-0000-0000-000000000001', 'SAGA_STARTED', '{"saga_type": "POLICY_CREATION", "trigger": "USER_REQUEST"}', 'aaaaaaaa-0000-0000-0000-000000000002', 'workflow-service', 'CORR-POL-001', NOW() - INTERVAL '30 days'),
  ('wwwwwwww-0000-0000-0000-000000000002', 'ssssssss-1000-0000-0000-000000000001', 'STEP_COMPLETED', '{"step": "CREATE_POLICY", "duration_ms": 1250}', 'aaaaaaaa-0000-0000-0000-000000000002', 'policy-service', 'CORR-POL-001', NOW() - INTERVAL '30 days'),
  ('wwwwwwww-0000-0000-0000-000000000003', 'ssssssss-1000-0000-0000-000000000001', 'SAGA_COMPLETED', '{"total_duration_ms": 5430, "steps_count": 3}', 'aaaaaaaa-0000-0000-0000-000000000002', 'workflow-service', 'CORR-POL-001', NOW() - INTERVAL '29 days'),
  ('wwwwwwww-0000-0000-0000-000000000004', 'ssssssss-1000-0000-0000-000000000004', 'SLA_WARNING', '{"claim_id": "xxxxxxxx-0000-0000-0000-000000000003", "sla_deadline": "2024-03-12T10:00:00Z", "current_time": "2024-03-11T18:00:00Z"}', 'SYSTEM', 'workflow-service', 'CORR-CLM-002', NOW() - INTERVAL '1 day'),
  ('wwwwwwww-0000-0000-0000-000000000005', 'ssssssss-1000-0000-0000-000000000005', 'SAGA_FAILED', '{"failure_reason": "Payment processing failed after 3 retries", "failed_step": "PROCESS_PAYMENT"}', 'SYSTEM', 'workflow-service', 'CORR-PAY-001', NOW() - INTERVAL '7 days'),
  ('wwwwwwww-0000-0000-0000-000000000006', 'ssssssss-1000-0000-0000-000000000006', 'COMPENSATION_STARTED', '{"reason": "Policy suspension requested", "compensation_steps": ["REVERSE_BILLING", "NOTIFY_CLIENT"]}', 'aaaaaaaa-0000-0000-0000-000000000002', 'workflow-service', 'CORR-POL-003', NOW() - INTERVAL '1 day')
ON CONFLICT (id) DO NOTHING;

-- Insert SLA Monitoring
INSERT INTO sla_monitoring (id, resource_type, resource_id, sla_type, deadline, status, escalated, escalated_to, escalation_level, created_at, updated_at) VALUES
  ('mmmmmmmm-1000-0000-0000-000000000001', 'CLAIM', 'xxxxxxxx-0000-0000-0000-000000000003', 'REVIEW_SLA', NOW() + INTERVAL '1 day', 'ACTIVE', false, NULL, 0, NOW() - INTERVAL '10 days', NOW() - INTERVAL '2 days'),
  ('mmmmmmmm-1000-0000-0000-000000000002', 'CLAIM', 'xxxxxxxx-0000-0000-0000-000000000004', 'REVIEW_SLA', NOW() + INTERVAL '2 days', 'ACTIVE', false, NULL, 0, NOW() - INTERVAL '5 days', NOW() - INTERVAL '5 days'),
  ('mmmmmmmm-1000-0000-0000-000000000003', 'SAGA', 'ssssssss-1000-0000-0000-000000000004', 'COMPLETION_SLA', NOW() + INTERVAL '3 days', 'WARNING', true, 'aaaaaaaa-0000-0000-0000-000000000001', 1, NOW() - INTERVAL '10 days', NOW() - INTERVAL '1 day')
ON CONFLICT (id) DO NOTHING;