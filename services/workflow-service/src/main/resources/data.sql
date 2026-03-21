-- ============================================================
-- Workflow Service - Complete Database Schema & Seed Data
-- ============================================================

-- ============================================================
-- SAGA ORCHESTRATION TABLES
-- ============================================================

-- SAGA Transactions Table
CREATE TABLE IF NOT EXISTS saga_transactions (
    id UUID PRIMARY KEY,
    saga_type VARCHAR(100) NOT NULL,
    status VARCHAR(50) NOT NULL,
    initiated_by UUID NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_saga_transactions_status ON saga_transactions(status);
CREATE INDEX IF NOT EXISTS idx_saga_transactions_type ON saga_transactions(saga_type);
CREATE INDEX IF NOT EXISTS idx_saga_transactions_initiated_by ON saga_transactions(initiated_by);

-- SAGA Steps Table
CREATE TABLE IF NOT EXISTS saga_steps (
    id UUID PRIMARY KEY,
    saga_transaction_id UUID NOT NULL,
    service_name VARCHAR(100) NOT NULL,
    action VARCHAR(100) NOT NULL,
    status VARCHAR(50) NOT NULL,
    compensation_action VARCHAR(100),
    started_at TIMESTAMP,
    completed_at TIMESTAMP,
    error_details TEXT,
    CONSTRAINT fk_saga_step_transaction FOREIGN KEY (saga_transaction_id) REFERENCES saga_transactions(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_saga_steps_transaction ON saga_steps(saga_transaction_id);
CREATE INDEX IF NOT EXISTS idx_saga_steps_status ON saga_steps(status);
CREATE INDEX IF NOT EXISTS idx_saga_steps_service ON saga_steps(service_name);

-- ============================================================
-- AUDIT TRAIL TABLES
-- ============================================================

-- Audit Logs Table
CREATE TABLE IF NOT EXISTS audit_logs (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    username VARCHAR(100) NOT NULL,
    action VARCHAR(50) NOT NULL,
    entity_type VARCHAR(50) NOT NULL,
    entity_id UUID NOT NULL,
    old_value TEXT,
    new_value TEXT,
    reason TEXT,
    timestamp TIMESTAMP NOT NULL,
    ip_address VARCHAR(50),
    user_agent VARCHAR(255)
);

CREATE INDEX IF NOT EXISTS idx_audit_logs_user_id ON audit_logs(user_id);
CREATE INDEX IF NOT EXISTS idx_audit_logs_entity ON audit_logs(entity_type, entity_id);
CREATE INDEX IF NOT EXISTS idx_audit_logs_action ON audit_logs(action);
CREATE INDEX IF NOT EXISTS idx_audit_logs_timestamp ON audit_logs(timestamp DESC);
CREATE INDEX IF NOT EXISTS idx_audit_logs_entity_timestamp ON audit_logs(entity_type, entity_id, timestamp DESC);

-- ============================================================
-- SLA MANAGEMENT TABLES
-- ============================================================

-- SLA Definitions Table
CREATE TABLE IF NOT EXISTS sla_definitions (
    id UUID PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL,
    entity_type VARCHAR(50) NOT NULL,
    description TEXT,
    duration_hours INTEGER NOT NULL,
    auto_escalate BOOLEAN NOT NULL DEFAULT true,
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_sla_definitions_entity_type ON sla_definitions(entity_type);
CREATE INDEX IF NOT EXISTS idx_sla_definitions_active ON sla_definitions(active);
CREATE INDEX IF NOT EXISTS idx_sla_definitions_entity_active ON sla_definitions(entity_type, active);

-- SLA Violations Table
CREATE TABLE IF NOT EXISTS sla_violations (
    id UUID PRIMARY KEY,
    sla_definition_id UUID NOT NULL,
    entity_id UUID NOT NULL,
    entity_type VARCHAR(50) NOT NULL,
    deadline TIMESTAMP NOT NULL,
    violated_at TIMESTAMP NOT NULL,
    delay_minutes BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL,
    escalated BOOLEAN NOT NULL DEFAULT false,
    escalation_id UUID,
    notes TEXT,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_sla_violation_definition FOREIGN KEY (sla_definition_id) REFERENCES sla_definitions(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_sla_violations_entity ON sla_violations(entity_type, entity_id);
CREATE INDEX IF NOT EXISTS idx_sla_violations_status ON sla_violations(status);
CREATE INDEX IF NOT EXISTS idx_sla_violations_escalated ON sla_violations(escalated);
CREATE INDEX IF NOT EXISTS idx_sla_violations_definition ON sla_violations(sla_definition_id);
CREATE INDEX IF NOT EXISTS idx_sla_violations_deadline ON sla_violations(deadline);

-- ============================================================
-- ESCALATION MANAGEMENT TABLES
-- ============================================================

-- Escalations Table
CREATE TABLE IF NOT EXISTS escalations (
    id UUID PRIMARY KEY,
    entity_id UUID NOT NULL,
    entity_type VARCHAR(50) NOT NULL,
    level VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    reason VARCHAR(100) NOT NULL,
    description TEXT,
    assigned_to UUID,
    assigned_to_name VARCHAR(100),
    sla_violation_id UUID,
    created_at TIMESTAMP NOT NULL,
    resolved_at TIMESTAMP,
    resolved_by UUID,
    resolution TEXT
);

CREATE INDEX IF NOT EXISTS idx_escalations_entity ON escalations(entity_type, entity_id);
CREATE INDEX IF NOT EXISTS idx_escalations_status ON escalations(status);
CREATE INDEX IF NOT EXISTS idx_escalations_assigned_to ON escalations(assigned_to);
CREATE INDEX IF NOT EXISTS idx_escalations_sla_violation ON escalations(sla_violation_id);
CREATE INDEX IF NOT EXISTS idx_escalations_created_at ON escalations(created_at DESC);
CREATE INDEX IF NOT EXISTS idx_escalations_level ON escalations(level);

-- ============================================================
-- SEED DATA - SAGA ORCHESTRATION
-- ============================================================

-- SAGA Transactions
INSERT INTO saga_transactions (id, saga_type, status, initiated_by, created_at, updated_at) VALUES
  ('70000000-0000-0000-0000-000000000001', 'POLICY_CREATION', 'COMPLETED', '20000000-0000-0000-0000-000000000003', NOW() - INTERVAL '5 days', NOW() - INTERVAL '5 days'),
  ('70000000-0000-0000-0000-000000000002', 'POLICY_CREATION', 'IN_PROGRESS', '20000000-0000-0000-0000-000000000004', NOW() - INTERVAL '2 hours', NOW() - INTERVAL '1 hour'),
  ('70000000-0000-0000-0000-000000000003', 'CLAIM_PROCESSING', 'COMPLETED', '20000000-0000-0000-0000-000000000003', NOW() - INTERVAL '10 days', NOW() - INTERVAL '10 days'),
  ('70000000-0000-0000-0000-000000000004', 'CLAIM_PROCESSING', 'IN_PROGRESS', '20000000-0000-0000-0000-000000000004', NOW() - INTERVAL '3 days', NOW() - INTERVAL '1 day'),
  ('70000000-0000-0000-0000-000000000005', 'PAYMENT_PROCESSING', 'COMPLETED', '20000000-0000-0000-0000-000000000007', NOW() - INTERVAL '15 days', NOW() - INTERVAL '15 days'),
  ('70000000-0000-0000-0000-000000000006', 'PAYMENT_PROCESSING', 'FAILED', '20000000-0000-0000-0000-000000000007', NOW() - INTERVAL '7 days', NOW() - INTERVAL '7 days'),
  ('70000000-0000-0000-0000-000000000007', 'POLICY_RENEWAL', 'COMPLETED', '20000000-0000-0000-0000-000000000003', NOW() - INTERVAL '20 days', NOW() - INTERVAL '20 days'),
  ('70000000-0000-0000-0000-000000000008', 'POLICY_CANCELLATION', 'COMPLETED', '20000000-0000-0000-0000-000000000004', NOW() - INTERVAL '30 days', NOW() - INTERVAL '30 days')
ON CONFLICT (id) DO NOTHING;

-- SAGA Steps
INSERT INTO saga_steps (
  id, saga_transaction_id, service_name, action, status,
  compensation_action, started_at, completed_at, error_details
) VALUES
  -- Policy Creation SAGA (COMPLETED)
  ('71000000-0000-0000-0000-000000000001', '70000000-0000-0000-0000-000000000001', 'policy-service', 'CREATE_POLICY', 'COMPLETED', 'DELETE_POLICY', NOW() - INTERVAL '5 days', NOW() - INTERVAL '5 days', NULL),
  ('71000000-0000-0000-0000-000000000002', '70000000-0000-0000-0000-000000000001', 'billing-service', 'GENERATE_INVOICE', 'COMPLETED', 'CANCEL_INVOICE', NOW() - INTERVAL '5 days', NOW() - INTERVAL '5 days', NULL),
  ('71000000-0000-0000-0000-000000000003', '70000000-0000-0000-0000-000000000001', 'notification-service', 'SEND_NOTIFICATION', 'COMPLETED', NULL, NOW() - INTERVAL '5 days', NOW() - INTERVAL '5 days', NULL),
  
  -- Policy Creation SAGA (IN_PROGRESS)
  ('71000000-0000-0000-0000-000000000004', '70000000-0000-0000-0000-000000000002', 'policy-service', 'CREATE_POLICY', 'COMPLETED', 'DELETE_POLICY', NOW() - INTERVAL '2 hours', NOW() - INTERVAL '2 hours', NULL),
  ('71000000-0000-0000-0000-000000000005', '70000000-0000-0000-0000-000000000002', 'billing-service', 'GENERATE_INVOICE', 'IN_PROGRESS', 'CANCEL_INVOICE', NOW() - INTERVAL '1 hour', NULL, NULL),
  
  -- Claim Processing SAGA (COMPLETED)
  ('71000000-0000-0000-0000-000000000006', '70000000-0000-0000-0000-000000000003', 'claims-service', 'REVIEW_CLAIM', 'COMPLETED', NULL, NOW() - INTERVAL '10 days', NOW() - INTERVAL '10 days', NULL),
  ('71000000-0000-0000-0000-000000000007', '70000000-0000-0000-0000-000000000003', 'claims-service', 'APPROVE_CLAIM', 'COMPLETED', 'REJECT_CLAIM', NOW() - INTERVAL '10 days', NOW() - INTERVAL '10 days', NULL),
  ('71000000-0000-0000-0000-000000000008', '70000000-0000-0000-0000-000000000003', 'billing-service', 'PROCESS_PAYOUT', 'COMPLETED', NULL, NOW() - INTERVAL '10 days', NOW() - INTERVAL '10 days', NULL),
  ('71000000-0000-0000-0000-000000000009', '70000000-0000-0000-0000-000000000003', 'notification-service', 'SEND_NOTIFICATION', 'COMPLETED', NULL, NOW() - INTERVAL '10 days', NOW() - INTERVAL '10 days', NULL),
  
  -- Claim Processing SAGA (IN_PROGRESS)
  ('71000000-0000-0000-0000-000000000010', '70000000-0000-0000-0000-000000000004', 'claims-service', 'REVIEW_CLAIM', 'COMPLETED', NULL, NOW() - INTERVAL '3 days', NOW() - INTERVAL '2 days', NULL),
  ('71000000-0000-0000-0000-000000000011', '70000000-0000-0000-0000-000000000004', 'claims-service', 'APPROVE_CLAIM', 'IN_PROGRESS', 'REJECT_CLAIM', NOW() - INTERVAL '1 day', NULL, NULL),
  
  -- Payment Processing SAGA (COMPLETED)
  ('71000000-0000-0000-0000-000000000012', '70000000-0000-0000-0000-000000000005', 'billing-service', 'VALIDATE_PAYMENT', 'COMPLETED', NULL, NOW() - INTERVAL '15 days', NOW() - INTERVAL '15 days', NULL),
  ('71000000-0000-0000-0000-000000000013', '70000000-0000-0000-0000-000000000005', 'billing-service', 'PROCESS_PAYMENT', 'COMPLETED', 'REFUND_PAYMENT', NOW() - INTERVAL '15 days', NOW() - INTERVAL '15 days', NULL),
  ('71000000-0000-0000-0000-000000000014', '70000000-0000-0000-0000-000000000005', 'notification-service', 'SEND_NOTIFICATION', 'COMPLETED', NULL, NOW() - INTERVAL '15 days', NOW() - INTERVAL '15 days', NULL),
  
  -- Payment Processing SAGA (FAILED)
  ('71000000-0000-0000-0000-000000000015', '70000000-0000-0000-0000-000000000006', 'billing-service', 'VALIDATE_PAYMENT', 'COMPLETED', NULL, NOW() - INTERVAL '7 days', NOW() - INTERVAL '7 days', NULL),
  ('71000000-0000-0000-0000-000000000016', '70000000-0000-0000-0000-000000000006', 'billing-service', 'PROCESS_PAYMENT', 'FAILED', 'REFUND_PAYMENT', NOW() - INTERVAL '7 days', NOW() - INTERVAL '7 days', 'Payment gateway timeout'),
  
  -- Policy Renewal SAGA (COMPLETED)
  ('71000000-0000-0000-0000-000000000017', '70000000-0000-0000-0000-000000000007', 'policy-service', 'RENEW_POLICY', 'COMPLETED', NULL, NOW() - INTERVAL '20 days', NOW() - INTERVAL '20 days', NULL),
  ('71000000-0000-0000-0000-000000000018', '70000000-0000-0000-0000-000000000007', 'billing-service', 'GENERATE_INVOICE', 'COMPLETED', 'CANCEL_INVOICE', NOW() - INTERVAL '20 days', NOW() - INTERVAL '20 days', NULL),
  ('71000000-0000-0000-0000-000000000019', '70000000-0000-0000-0000-000000000007', 'notification-service', 'SEND_NOTIFICATION', 'COMPLETED', NULL, NOW() - INTERVAL '20 days', NOW() - INTERVAL '20 days', NULL),
  
  -- Policy Cancellation SAGA (COMPLETED)
  ('71000000-0000-0000-0000-000000000020', '70000000-0000-0000-0000-000000000008', 'policy-service', 'CANCEL_POLICY', 'COMPLETED', NULL, NOW() - INTERVAL '30 days', NOW() - INTERVAL '30 days', NULL),
  ('71000000-0000-0000-0000-000000000021', '70000000-0000-0000-0000-000000000008', 'billing-service', 'CANCEL_INVOICE', 'COMPLETED', NULL, NOW() - INTERVAL '30 days', NOW() - INTERVAL '30 days', NULL),
  ('71000000-0000-0000-0000-000000000022', '70000000-0000-0000-0000-000000000008', 'notification-service', 'SEND_NOTIFICATION', 'COMPLETED', NULL, NOW() - INTERVAL '30 days', NOW() - INTERVAL '30 days', NULL)
ON CONFLICT (id) DO NOTHING;

-- ============================================================
-- SEED DATA - SLA DEFINITIONS
-- ============================================================

-- Claims Processing SLA (48 hours)
INSERT INTO sla_definitions (id, name, entity_type, description, duration_hours, auto_escalate, active, created_at, updated_at)
VALUES (
    '80000000-0000-0000-0000-000000000001',
    'Claims Processing SLA',
    'CLAIM',
    'Claims must be processed within 48 hours of submission. Automatic escalation will be triggered if the deadline is exceeded.',
    48,
    true,
    true,
    NOW(),
    NOW()
) ON CONFLICT (name) DO NOTHING;

-- Policy Approval SLA (24 hours)
INSERT INTO sla_definitions (id, name, entity_type, description, duration_hours, auto_escalate, active, created_at, updated_at)
VALUES (
    '80000000-0000-0000-0000-000000000002',
    'Policy Approval SLA',
    'POLICY',
    'Policy applications must be approved or rejected within 24 hours of submission.',
    24,
    true,
    true,
    NOW(),
    NOW()
) ON CONFLICT (name) DO NOTHING;

-- Invoice Payment SLA (72 hours)
INSERT INTO sla_definitions (id, name, entity_type, description, duration_hours, auto_escalate, active, created_at, updated_at)
VALUES (
    '80000000-0000-0000-0000-000000000003',
    'Invoice Payment SLA',
    'INVOICE',
    'Invoices should be paid within 72 hours of generation. Escalation for overdue invoices.',
    72,
    false,
    true,
    NOW(),
    NOW()
) ON CONFLICT (name) DO NOTHING;

-- ============================================================
-- SEED DATA - SAMPLE AUDIT LOGS
-- ============================================================

INSERT INTO audit_logs (id, user_id, username, action, entity_type, entity_id, old_value, new_value, reason, timestamp, ip_address, user_agent) VALUES
  -- System initialization
  ('90000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000000', 'SYSTEM', 'SAGA_STARTED', 'SAGA', '70000000-0000-0000-0000-000000000001', NULL, 'POLICY_CREATION saga initiated', 'System initialization', NOW() - INTERVAL '5 days', '127.0.0.1', 'WorkflowService/1.0'),
  
  -- Policy actions
  ('90000000-0000-0000-0000-000000000002', '20000000-0000-0000-0000-000000000003', 'agent.hassan', 'POLICY_CREATED', 'POLICY', '50000000-0000-0000-0000-000000000001', NULL, '{"policyNumber":"POL-VEH-2026-001","status":"ACTIVE"}', 'New vehicle policy created', NOW() - INTERVAL '30 days', '192.168.1.100', 'Mozilla/5.0'),
  ('90000000-0000-0000-0000-000000000003', '20000000-0000-0000-0000-000000000004', 'agent.amina', 'POLICY_APPROVED', 'POLICY', '50000000-0000-0000-0000-000000000002', '{"status":"PENDING"}', '{"status":"ACTIVE"}', 'Policy approved after review', NOW() - INTERVAL '60 days', '192.168.1.101', 'Mozilla/5.0'),
  ('90000000-0000-0000-0000-000000000004', '20000000-0000-0000-0000-000000000003', 'agent.hassan', 'POLICY_RENEWED', 'POLICY', '50000000-0000-0000-0000-000000000007', '{"endDate":"2025-12-31"}', '{"endDate":"2026-12-31"}', 'Annual renewal', NOW() - INTERVAL '20 days', '192.168.1.100', 'Mozilla/5.0'),
  ('90000000-0000-0000-0000-000000000005', '20000000-0000-0000-0000-000000000004', 'agent.amina', 'POLICY_CANCELLED', 'POLICY', '50000000-0000-0000-0000-000000000018', '{"status":"ACTIVE"}', '{"status":"CANCELLED"}', 'Client request', NOW() - INTERVAL '30 days', '192.168.1.101', 'Mozilla/5.0'),
  
  -- Claim actions
  ('90000000-0000-0000-0000-000000000006', '20000000-0000-0000-0000-000000000011', 'client.ahmed', 'CLAIM_SUBMITTED', 'CLAIM', '60000000-0000-0000-0000-000000000001', NULL, '{"claimNumber":"CLM-2026-001","status":"SUBMITTED"}', 'Vehicle collision claim', NOW() - INTERVAL '2 days', '10.0.0.50', 'Mozilla/5.0'),
  ('90000000-0000-0000-0000-000000000007', '20000000-0000-0000-0000-000000000003', 'agent.hassan', 'CLAIM_REVIEWED', 'CLAIM', '60000000-0000-0000-0000-000000000004', '{"status":"SUBMITTED"}', '{"status":"UNDER_REVIEW"}', 'Initial review completed', NOW() - INTERVAL '8 days', '192.168.1.100', 'Mozilla/5.0'),
  ('90000000-0000-0000-0000-000000000008', '20000000-0000-0000-0000-000000000004', 'agent.amina', 'CLAIM_APPROVED', 'CLAIM', '60000000-0000-0000-0000-000000000007', '{"status":"UNDER_REVIEW"}', '{"status":"APPROVED","approvedAmount":1150}', 'Claim approved', NOW() - INTERVAL '5 days', '192.168.1.101', 'Mozilla/5.0'),
  ('90000000-0000-0000-0000-000000000009', '20000000-0000-0000-0000-000000000003', 'agent.hassan', 'CLAIM_REJECTED', 'CLAIM', '60000000-0000-0000-0000-000000000015', '{"status":"UNDER_REVIEW"}', '{"status":"REJECTED"}', 'Pre-existing damage', NOW() - INTERVAL '25 days', '192.168.1.100', 'Mozilla/5.0'),
  ('90000000-0000-0000-0000-000000000010', '20000000-0000-0000-0000-000000000007', 'finance.omar', 'CLAIM_PAID', 'CLAIM', '60000000-0000-0000-0000-000000000012', '{"status":"APPROVED"}', '{"status":"PAID"}', 'Payment processed', NOW() - INTERVAL '10 days', '192.168.1.150', 'Mozilla/5.0'),
  
  -- Billing actions
  ('90000000-0000-0000-0000-000000000011', '20000000-0000-0000-0000-000000000007', 'finance.omar', 'INVOICE_GENERATED', 'INVOICE', '30000000-0000-0000-0000-000000000001', NULL, '{"invoiceNumber":"INV-2026-001","totalAmount":1440}', 'Policy premium invoice', NOW() - INTERVAL '5 days', '192.168.1.150', 'Mozilla/5.0'),
  ('90000000-0000-0000-0000-000000000012', '20000000-0000-0000-0000-000000000007', 'finance.omar', 'PAYMENT_RECEIVED', 'PAYMENT', '31000000-0000-0000-0000-000000000001', NULL, '{"amount":6000,"method":"CREDIT_CARD"}', 'Payment completed', NOW() - INTERVAL '10 days', '192.168.1.150', 'Mozilla/5.0'),
  ('90000000-0000-0000-0000-000000000013', '20000000-0000-0000-0000-000000000007', 'finance.omar', 'PAYMENT_FAILED', 'PAYMENT', '31000000-0000-0000-0000-000000000009', '{"status":"PENDING"}', '{"status":"FAILED"}', 'Card declined', NOW() - INTERVAL '6 days', '192.168.1.150', 'Mozilla/5.0'),
  ('90000000-0000-0000-0000-000000000014', '20000000-0000-0000-0000-000000000008', 'finance.laila', 'INVOICE_OVERDUE', 'INVOICE', '30000000-0000-0000-0000-000000000013', '{"status":"PENDING"}', '{"status":"OVERDUE"}', 'Payment deadline passed', NOW() - INTERVAL '5 days', '192.168.1.151', 'Mozilla/5.0'),
  ('90000000-0000-0000-0000-000000000015', '20000000-0000-0000-0000-000000000007', 'finance.omar', 'PAYMENT_REFUNDED', 'PAYMENT', '31000000-0000-0000-0000-000000000011', '{"status":"COMPLETED"}', '{"status":"REFUNDED"}', 'Policy cancelled', NOW() - INTERVAL '40 days', '192.168.1.150', 'Mozilla/5.0'),
  
  -- User management actions
  ('90000000-0000-0000-0000-000000000016', '20000000-0000-0000-0000-000000000001', 'admin', 'USER_CREATED', 'USER', '20000000-0000-0000-0000-000000000006', NULL, '{"username":"agent.nadia","role":"AGENT"}', 'New agent onboarded', NOW() - INTERVAL '45 days', '192.168.1.10', 'Mozilla/5.0'),
  ('90000000-0000-0000-0000-000000000017', '20000000-0000-0000-0000-000000000001', 'admin', 'USER_DEACTIVATED', 'USER', '20000000-0000-0000-0000-000000000006', '{"active":true}', '{"active":false}', 'Temporary suspension', NOW() - INTERVAL '30 days', '192.168.1.10', 'Mozilla/5.0'),
  ('90000000-0000-0000-0000-000000000018', '20000000-0000-0000-0000-000000000009', 'manager.youssef', 'REPORT_GENERATED', 'REPORT', '00000000-0000-0000-0000-000000000001', NULL, '{"type":"MONTHLY_CLAIMS","period":"2026-03"}', 'Monthly claims report', NOW() - INTERVAL '1 day', '192.168.1.200', 'Mozilla/5.0'),
  
  -- Client actions
  ('90000000-0000-0000-0000-000000000019', '20000000-0000-0000-0000-000000000011', 'client.ahmed', 'CLIENT_REGISTERED', 'CLIENT', 'cccccccc-0000-0000-0000-000000000001', NULL, '{"clientNumber":"CLI-2024-001"}', 'Self registration', NOW() - INTERVAL '90 days', '10.0.0.50', 'Mozilla/5.0'),
  ('90000000-0000-0000-0000-000000000020', '20000000-0000-0000-0000-000000000003', 'agent.hassan', 'CLIENT_UPDATED', 'CLIENT', 'cccccccc-0000-0000-0000-000000000003', '{"phone":"0663456789"}', '{"phone":"0663456790"}', 'Phone number updated', NOW() - INTERVAL '15 days', '192.168.1.100', 'Mozilla/5.0')
ON CONFLICT (id) DO NOTHING;

-- ============================================================
-- COMMENTS & DOCUMENTATION
-- ============================================================

COMMENT ON TABLE saga_transactions IS 'Stores SAGA orchestration transactions for distributed workflows';
COMMENT ON TABLE saga_steps IS 'Individual steps within a SAGA transaction with compensation support';
COMMENT ON TABLE audit_logs IS 'Complete audit trail for all system actions across all services';
COMMENT ON TABLE sla_definitions IS 'Configurable SLA rules per entity type with auto-escalation settings';
COMMENT ON TABLE sla_violations IS 'Tracks SLA breaches with delay metrics and escalation status';
COMMENT ON TABLE escalations IS 'Multi-level escalation system for SLA violations and critical issues';

COMMENT ON COLUMN audit_logs.action IS 'Action type from AuditAction enum (40+ predefined actions)';
COMMENT ON COLUMN audit_logs.old_value IS 'State before the action (JSON or text)';
COMMENT ON COLUMN audit_logs.new_value IS 'State after the action (JSON or text)';
COMMENT ON COLUMN sla_definitions.duration_hours IS 'SLA duration in hours (e.g., 48 for claims)';
COMMENT ON COLUMN sla_definitions.auto_escalate IS 'Whether to automatically create escalation on violation';
COMMENT ON COLUMN sla_violations.delay_minutes IS 'How many minutes past the deadline the violation occurred';
COMMENT ON COLUMN escalations.level IS 'Escalation level: LEVEL_1 (Agent), LEVEL_2 (Manager), LEVEL_3 (Director)';
