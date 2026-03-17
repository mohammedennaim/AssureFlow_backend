-- ============================================================
-- Notification Service - Seed data aligned with current entities
-- ============================================================

INSERT INTO notification_templates (id, notification_id, name, body_template, status) VALUES
  (
    '80000000-0000-0000-0000-000000000001',
    '80000000-0000-0000-0000-000000000101',
    'POLICY_CREATED_TEMPLATE',
    'Bonjour {{clientName}}, votre police {{policyNumber}} a ete creee.',
    'SENT'
  )
ON CONFLICT (id) DO NOTHING;

INSERT INTO notifications (
  id,
  type,
  channel,
  recipient,
  link_name,
  subject,
  content,
  status,
  policy_id,
  sent_at,
  created_at
) VALUES
  (
    '81000000-0000-0000-0000-000000000001',
    'POLICY_CREATED',
    'EMAIL',
    'client.demo@assureflow.com',
    NULL,
    'Creation de police',
    'Votre police a ete creee avec succes.',
    'DELIVERED',
    '40000000-0000-0000-0000-000000000001',
    NOW(),
    NOW()
  )
ON CONFLICT (id) DO NOTHING;

INSERT INTO notification_logs (id, notification_id, timestamp, status) VALUES
  (
    '82000000-0000-0000-0000-000000000001',
    '81000000-0000-0000-0000-000000000001',
    NOW(),
    'DELIVERED'
  )
ON CONFLICT (id) DO NOTHING;