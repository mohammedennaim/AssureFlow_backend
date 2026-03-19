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

-- Sample SAGA Transaction
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

-- Sample SAGA Steps
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

-- Sample audit log for system initialization
INSERT INTO audit_logs (id, user_id, username, action, entity_type, entity_id, old_value, new_value, reason, timestamp, ip_address, user_agent)
VALUES (
    '90000000-0000-0000-0000-000000000001',
    '00000000-0000-0000-0000-000000000000',
    'SYSTEM',
    'SAGA_STARTED',
    'SAGA',
    '70000000-0000-0000-0000-000000000001',
    NULL,
    'POLICY_CREATION saga initiated',
    'System initialization',
    NOW() - INTERVAL '5 minutes',
    '127.0.0.1',
    'WorkflowService/1.0'
) ON CONFLICT (id) DO NOTHING;

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
