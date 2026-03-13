-- ============================================================
-- Notification Service - Fake Data
-- Notification templates and sent notifications
-- ============================================================

-- Insert Notification Templates
INSERT INTO notification_templates (id, name, type, subject, body, variables, active, created_at, updated_at) VALUES
  ('tttttttt-0000-0000-0000-000000000001', 'POLICY_CREATED', 'EMAIL', 'Votre police d''assurance a été créée', 'Bonjour {{clientName}}, votre police {{policyNumber}} a été créée avec succès. Montant de la prime: {{premium}} MAD.', 'clientName,policyNumber,premium', true, NOW(), NOW()),
  ('tttttttt-0000-0000-0000-000000000002', 'INVOICE_GENERATED', 'EMAIL', 'Nouvelle facture disponible', 'Bonjour {{clientName}}, une nouvelle facture {{invoiceNumber}} d''un montant de {{amount}} MAD est disponible. Date d''échéance: {{dueDate}}.', 'clientName,invoiceNumber,amount,dueDate', true, NOW(), NOW()),
  ('tttttttt-0000-0000-0000-000000000003', 'PAYMENT_OVERDUE', 'EMAIL', 'Facture en retard - Action requise', 'Bonjour {{clientName}}, votre facture {{invoiceNumber}} est en retard de {{daysOverdue}} jours. Montant dû: {{amount}} MAD.', 'clientName,invoiceNumber,daysOverdue,amount', true, NOW(), NOW()),
  ('tttttttt-0000-0000-0000-000000000004', 'CLAIM_APPROVED', 'EMAIL', 'Votre sinistre a été approuvé', 'Bonjour {{clientName}}, votre sinistre {{claimNumber}} a été approuvé. Montant approuvé: {{approvedAmount}} MAD.', 'clientName,claimNumber,approvedAmount', true, NOW(), NOW()),
  ('tttttttt-0000-0000-0000-000000000005', 'CLAIM_REJECTED', 'EMAIL', 'Votre sinistre a été rejeté', 'Bonjour {{clientName}}, votre sinistre {{claimNumber}} a été rejeté. Raison: {{rejectionReason}}.', 'clientName,claimNumber,rejectionReason', true, NOW(), NOW()),
  ('tttttttt-0000-0000-0000-000000000006', 'PAYMENT_REMINDER', 'SMS', 'Rappel de paiement AssureFlow', 'Rappel: Votre facture {{invoiceNumber}} de {{amount}} MAD est due le {{dueDate}}. Payez sur assureflow.com', 'invoiceNumber,amount,dueDate', true, NOW(), NOW()),
  ('tttttttt-0000-0000-0000-000000000007', 'CLAIM_SLA_ESCALATION', 'EMAIL', 'Escalade SLA - Sinistre en retard', 'Le sinistre {{claimNumber}} dépasse le SLA de {{slaHours}}h. Action immédiate requise.', 'claimNumber,slaHours,assignedTo', true, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- Insert Sent Notifications
INSERT INTO notifications (id, template_id, recipient_email, recipient_phone, type, subject, body, status, sent_date, delivery_date, error_message, retry_count, created_at) VALUES
  ('nnnnnnnn-0000-0000-0000-000000000001', 'tttttttt-0000-0000-0000-000000000001', 'ahmed.benali@gmail.com', '0661234567', 'EMAIL', 'Votre police d''assurance a été créée', 'Bonjour Ahmed Benali, votre police POL-AUTO-001 a été créée avec succès. Montant de la prime: 1200.00 MAD.', 'DELIVERED', NOW() - INTERVAL '30 days', NOW() - INTERVAL '30 days', NULL, 0, NOW() - INTERVAL '30 days'),
  ('nnnnnnnn-0000-0000-0000-000000000002', 'tttttttt-0000-0000-0000-000000000002', 'ahmed.benali@gmail.com', '0661234567', 'EMAIL', 'Nouvelle facture disponible', 'Bonjour Ahmed Benali, une nouvelle facture INV-2024-001 d''un montant de 1200.00 MAD est disponible. Date d''échéance: 2024-01-31.', 'DELIVERED', NOW() - INTERVAL '30 days', NOW() - INTERVAL '30 days', NULL, 0, NOW() - INTERVAL '30 days'),
  ('nnnnnnnn-0000-0000-0000-000000000003', 'tttttttt-0000-0000-0000-000000000004', 'ahmed.benali@gmail.com', '0661234567', 'EMAIL', 'Votre sinistre a été approuvé', 'Bonjour Ahmed Benali, votre sinistre CLM-2024-001 a été approuvé. Montant approuvé: 8000.00 MAD.', 'DELIVERED', NOW() - INTERVAL '10 days', NOW() - INTERVAL '10 days', NULL, 0, NOW() - INTERVAL '10 days'),
  ('nnnnnnnn-0000-0000-0000-000000000004', 'tttttttt-0000-0000-0000-000000000003', 'youssef.nejjari@gmail.com', '0663456789', 'EMAIL', 'Facture en retard - Action requise', 'Bonjour Youssef Nejjari, votre facture INV-2024-005 est en retard de 15 jours. Montant dû: 1500.00 MAD.', 'DELIVERED', NOW() - INTERVAL '5 days', NOW() - INTERVAL '5 days', NULL, 0, NOW() - INTERVAL '5 days'),
  ('nnnnnnnn-0000-0000-0000-000000000005', 'tttttttt-0000-0000-0000-000000000006', NULL, '0663456789', 'SMS', 'Rappel de paiement AssureFlow', 'Rappel: Votre facture INV-2024-005 de 1500.00 MAD est due le 2024-02-29. Payez sur assureflow.com', 'DELIVERED', NOW() - INTERVAL '3 days', NOW() - INTERVAL '3 days', NULL, 0, NOW() - INTERVAL '3 days'),
  ('nnnnnnnn-0000-0000-0000-000000000006', 'tttttttt-0000-0000-0000-000000000005', 'ahmed.benali@gmail.com', '0661234567', 'EMAIL', 'Votre sinistre a été rejeté', 'Bonjour Ahmed Benali, votre sinistre CLM-2024-005 a été rejeté. Raison: Insufficient evidence provided.', 'DELIVERED', NOW() - INTERVAL '1 day', NOW() - INTERVAL '1 day', NULL, 0, NOW() - INTERVAL '1 day'),
  ('nnnnnnnn-0000-0000-0000-000000000007', 'tttttttt-0000-0000-0000-000000000007', 'leila.ops@assureflow.com', NULL, 'EMAIL', 'Escalade SLA - Sinistre en retard', 'Le sinistre CLM-2024-003 dépasse le SLA de 48h. Action immédiate requise.', 'PENDING', NOW() - INTERVAL '1 hour', NULL, NULL, 0, NOW() - INTERVAL '1 hour')
ON CONFLICT (id) DO NOTHING;

-- Insert Notification Events (for audit)
INSERT INTO notification_events (id, notification_id, event_type, event_data, created_at) VALUES
  ('vvvvvvvv-0000-0000-0000-000000000001', 'nnnnnnnn-0000-0000-0000-000000000001', 'EMAIL_SENT', '{"provider": "SMTP", "server": "smtp.assureflow.com"}', NOW() - INTERVAL '30 days'),
  ('vvvvvvvv-0000-0000-0000-000000000002', 'nnnnnnnn-0000-0000-0000-000000000001', 'EMAIL_DELIVERED', '{"delivery_time": "2024-02-15T10:30:00Z"}', NOW() - INTERVAL '30 days'),
  ('vvvvvvvv-0000-0000-0000-000000000003', 'nnnnnnnn-0000-0000-0000-000000000005', 'SMS_SENT', '{"provider": "Twilio", "message_sid": "SM1234567890"}', NOW() - INTERVAL '3 days'),
  ('vvvvvvvv-0000-0000-0000-000000000004', 'nnnnnnnn-0000-0000-0000-000000000007', 'EMAIL_QUEUED', '{"queue_position": 1, "estimated_send_time": "2024-03-10T15:00:00Z"}', NOW() - INTERVAL '1 hour')
ON CONFLICT (id) DO NOTHING;