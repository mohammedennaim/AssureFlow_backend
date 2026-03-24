-- ============================================================
-- Notification Service - Comprehensive Seed Data for Testing
-- ============================================================

-- Notification Templates
INSERT INTO notification_templates (id, notification_id, name, body_template, status) VALUES
  ('80000000-0000-0000-0000-000000000001', '80000000-0000-0000-0000-000000000101', 'POLICY_CREATED_TEMPLATE', 'Bonjour {{clientName}}, votre police {{policyNumber}} a été créée avec succès. Montant de la prime: {{premiumAmount}} MAD.', 'SENT'),
  ('80000000-0000-0000-0000-000000000002', '80000000-0000-0000-0000-000000000102', 'POLICY_APPROVED_TEMPLATE', 'Félicitations {{clientName}}! Votre police {{policyNumber}} a été approuvée. Couverture: {{coverageAmount}} MAD.', 'SENT'),
  ('80000000-0000-0000-0000-000000000003', '80000000-0000-0000-0000-000000000103', 'CLAIM_SUBMITTED_TEMPLATE', 'Votre réclamation {{claimNumber}} a été soumise avec succès. Montant estimé: {{estimatedAmount}} MAD.', 'SENT'),
  ('80000000-0000-0000-0000-000000000004', '80000000-0000-0000-0000-000000000104', 'CLAIM_APPROVED_TEMPLATE', 'Bonne nouvelle! Votre réclamation {{claimNumber}} a été approuvée. Montant approuvé: {{approvedAmount}} MAD.', 'SENT'),
  ('80000000-0000-0000-0000-000000000005', '80000000-0000-0000-0000-000000000105', 'PAYMENT_RECEIVED_TEMPLATE', 'Paiement reçu pour la facture {{invoiceNumber}}. Montant: {{amount}} MAD. Merci!', 'SENT'),
  ('80000000-0000-0000-0000-000000000006', '80000000-0000-0000-0000-000000000106', 'INVOICE_GENERATED_TEMPLATE', 'Nouvelle facture {{invoiceNumber}} générée. Montant total: {{totalAmount}} MAD. Date d''échéance: {{dueDate}}.', 'SENT'),
  ('80000000-0000-0000-0000-000000000007', '80000000-0000-0000-0000-000000000107', 'POLICY_EXPIRING_TEMPLATE', 'Attention! Votre police {{policyNumber}} expire le {{expiryDate}}. Veuillez la renouveler.', 'SENT'),
  ('80000000-0000-0000-0000-000000000008', '80000000-0000-0000-0000-000000000108', 'CLAIM_REJECTED_TEMPLATE', 'Votre réclamation {{claimNumber}} a été rejetée. Raison: {{reason}}.', 'SENT')
ON CONFLICT (id) DO NOTHING;

-- Notifications - Policy Related
INSERT INTO notifications (id, type, channel, recipient, link_name, subject, content, status, policy_id, sent_at, created_at, read) VALUES
  -- Recent notifications (non lues)
  ('81000000-0000-0000-0000-000000000001', 'POLICY_CREATED', 'EMAIL', 'ahmed.benali@gmail.com', NULL, 'Création de police', 'Votre police POL-VEH-2026-001 a été créée avec succès. Prime: 1200 MAD.', 'DELIVERED', '50000000-0000-0000-0000-000000000001', NOW() - INTERVAL '2 hours', NOW() - INTERVAL '2 hours', FALSE),
  ('81000000-0000-0000-0000-000000000002', 'POLICY_APPROVED', 'EMAIL', 'fatima.alami@gmail.com', NULL, 'Police approuvée', 'Félicitations! Votre police POL-VEH-2026-002 a été approuvée.', 'DELIVERED', '50000000-0000-0000-0000-000000000002', NOW() - INTERVAL '5 hours', NOW() - INTERVAL '5 hours', FALSE),
  ('81000000-0000-0000-0000-000000000003', 'POLICY_CREATED', 'SMS', '0663456789', NULL, 'Nouvelle police', 'Police POL-HOME-2026-001 créée. Couverture: 200000 MAD.', 'DELIVERED', '50000000-0000-0000-0000-000000000004', NOW() - INTERVAL '1 day', NOW() - INTERVAL '1 day', FALSE),
  ('81000000-0000-0000-0000-000000000004', 'POLICY_EXPIRING', 'EMAIL', 'leila.tahiri@gmail.com', NULL, 'Police expire bientôt', 'Votre police POL-HOME-2026-002 expire dans 30 jours.', 'DELIVERED', '50000000-0000-0000-0000-000000000005', NOW() - INTERVAL '3 days', NOW() - INTERVAL '3 days', FALSE),
  ('81000000-0000-0000-0000-000000000005', 'POLICY_RENEWED', 'EMAIL', 'ahmed.benali@gmail.com', NULL, 'Police renouvelée', 'Votre police POL-LIFE-2026-001 a été renouvelée avec succès.', 'DELIVERED', '50000000-0000-0000-0000-000000000007', NOW() - INTERVAL '5 days', NOW() - INTERVAL '5 days', TRUE),
  
  -- Pending notifications (non lues)
  ('81000000-0000-0000-0000-000000000006', 'POLICY_CREATED', 'EMAIL', 'nadia.benjelloun@gmail.com', NULL, 'Création de police', 'Votre police POL-HOME-2026-003 a été créée.', 'PENDING', '50000000-0000-0000-0000-000000000006', NULL, NOW() - INTERVAL '1 hour', FALSE),
  ('81000000-0000-0000-0000-000000000007', 'POLICY_EXPIRING', 'SMS', '0667890123', NULL, 'Expiration proche', 'Police POL-VEH-2026-003 expire dans 15 jours.', 'PENDING', '50000000-0000-0000-0000-000000000003', NULL, NOW() - INTERVAL '30 minutes', FALSE),
  
  -- Failed notifications (non lues)
  ('81000000-0000-0000-0000-000000000008', 'POLICY_CANCELLED', 'EMAIL', 'invalid@email', NULL, 'Police annulée', 'Votre police a été annulée.', 'FAILED', '50000000-0000-0000-0000-000000000018', NOW() - INTERVAL '2 days', NOW() - INTERVAL '2 days', FALSE)
ON CONFLICT (id) DO NOTHING;

-- Notifications - Claim Related
INSERT INTO notifications (id, type, channel, recipient, link_name, subject, content, status, policy_id, sent_at, created_at, read) VALUES
  ('81000000-0000-0000-0000-000000000009', 'CLAIM_SUBMITTED', 'EMAIL', 'ahmed.benali@gmail.com', NULL, 'Réclamation soumise', 'Votre réclamation CLM-2026-001 a été soumise. Montant: 2500 MAD.', 'DELIVERED', '50000000-0000-0000-0000-000000000001', NOW() - INTERVAL '2 days', NOW() - INTERVAL '2 days', FALSE),
  ('81000000-0000-0000-0000-000000000010', 'CLAIM_UNDER_REVIEW', 'EMAIL', 'youssef.nejjari@gmail.com', NULL, 'Réclamation en cours', 'Votre réclamation CLM-2026-004 est en cours d''examen.', 'DELIVERED', '50000000-0000-0000-0000-000000000003', NOW() - INTERVAL '8 days', NOW() - INTERVAL '8 days', FALSE),
  ('81000000-0000-0000-0000-000000000011', 'CLAIM_APPROVED', 'EMAIL', 'ahmed.benali@gmail.com', NULL, 'Réclamation approuvée', 'Votre réclamation CLM-2026-007 a été approuvée. Montant: 1150 MAD.', 'DELIVERED', '50000000-0000-0000-0000-000000000001', NOW() - INTERVAL '5 days', NOW() - INTERVAL '5 days', FALSE),
  ('81000000-0000-0000-0000-000000000012', 'CLAIM_PAID', 'EMAIL', 'karim.elfassi@gmail.com', NULL, 'Paiement effectué', 'Le paiement de votre réclamation CLM-2026-012 a été effectué.', 'DELIVERED', '50000000-0000-0000-0000-000000000003', NOW() - INTERVAL '10 days', NOW() - INTERVAL '10 days', TRUE),
  ('81000000-0000-0000-0000-000000000013', 'CLAIM_REJECTED', 'EMAIL', 'ahmed.benali@gmail.com', NULL, 'Réclamation rejetée', 'Votre réclamation CLM-2026-015 a été rejetée. Dommage pré-existant.', 'DELIVERED', '50000000-0000-0000-0000-000000000001', NOW() - INTERVAL '25 days', NOW() - INTERVAL '25 days', FALSE),
  ('81000000-0000-0000-0000-000000000014', 'CLAIM_SUBMITTED', 'SMS', '0666789012', NULL, 'Nouvelle réclamation', 'Réclamation CLM-2026-019 soumise. Montant: 5800 MAD.', 'DELIVERED', '50000000-0000-0000-0000-000000000006', NOW() - INTERVAL '1 day', NOW() - INTERVAL '1 day', FALSE)
ON CONFLICT (id) DO NOTHING;

-- Notifications - Billing Related
INSERT INTO notifications (id, type, channel, recipient, link_name, subject, content, status, policy_id, sent_at, created_at, read) VALUES
  ('81000000-0000-0000-0000-000000000015', 'INVOICE_GENERATED', 'EMAIL', 'ahmed.benali@gmail.com', NULL, 'Nouvelle facture', 'Facture INV-2026-001 générée. Montant: 1320 MAD. Échéance: 15 jours.', 'DELIVERED', '50000000-0000-0000-0000-000000000001', NOW() - INTERVAL '3 days', NOW() - INTERVAL '3 days', FALSE),
  ('81000000-0000-0000-0000-000000000016', 'PAYMENT_RECEIVED', 'EMAIL', 'leila.tahiri@gmail.com', NULL, 'Paiement reçu', 'Paiement de 1980 MAD reçu pour INV-2026-004. Merci!', 'DELIVERED', '50000000-0000-0000-0000-000000000005', NOW() - INTERVAL '7 days', NOW() - INTERVAL '7 days', TRUE),
  ('81000000-0000-0000-0000-000000000017', 'INVOICE_OVERDUE', 'EMAIL', 'fatima.alami@gmail.com', NULL, 'Facture en retard', 'Votre facture INV-2026-002 est en retard. Veuillez payer 880 MAD.', 'DELIVERED', '50000000-0000-0000-0000-000000000002', NOW() - INTERVAL '1 day', NOW() - INTERVAL '1 day', FALSE),
  ('81000000-0000-0000-0000-000000000018', 'PAYMENT_REMINDER', 'SMS', '0661234567', NULL, 'Rappel paiement', 'Rappel: Facture INV-2026-001 à payer avant le ' || TO_CHAR(CURRENT_DATE + INTERVAL '15 days', 'DD/MM/YYYY'), 'DELIVERED', '50000000-0000-0000-0000-000000000001', NOW() - INTERVAL '12 hours', NOW() - INTERVAL '12 hours', FALSE)
ON CONFLICT (id) DO NOTHING;

-- Notification Logs
INSERT INTO notification_logs (id, notification_id, timestamp, status) VALUES
  -- Successful deliveries
  ('82000000-0000-0000-0000-000000000001', '81000000-0000-0000-0000-000000000001', NOW() - INTERVAL '2 hours', 'DELIVERED'),
  ('82000000-0000-0000-0000-000000000002', '81000000-0000-0000-0000-000000000002', NOW() - INTERVAL '5 hours', 'DELIVERED'),
  ('82000000-0000-0000-0000-000000000003', '81000000-0000-0000-0000-000000000003', NOW() - INTERVAL '1 day', 'DELIVERED'),
  ('82000000-0000-0000-0000-000000000004', '81000000-0000-0000-0000-000000000004', NOW() - INTERVAL '3 days', 'DELIVERED'),
  ('82000000-0000-0000-0000-000000000005', '81000000-0000-0000-0000-000000000005', NOW() - INTERVAL '5 days', 'DELIVERED'),
  ('82000000-0000-0000-0000-000000000006', '81000000-0000-0000-0000-000000000009', NOW() - INTERVAL '2 days', 'DELIVERED'),
  ('82000000-0000-0000-0000-000000000007', '81000000-0000-0000-0000-000000000010', NOW() - INTERVAL '8 days', 'DELIVERED'),
  ('82000000-0000-0000-0000-000000000008', '81000000-0000-0000-0000-000000000011', NOW() - INTERVAL '5 days', 'DELIVERED'),
  ('82000000-0000-0000-0000-000000000009', '81000000-0000-0000-0000-000000000012', NOW() - INTERVAL '10 days', 'DELIVERED'),
  ('82000000-0000-0000-0000-000000000010', '81000000-0000-0000-0000-000000000013', NOW() - INTERVAL '25 days', 'DELIVERED'),
  ('82000000-0000-0000-0000-000000000011', '81000000-0000-0000-0000-000000000014', NOW() - INTERVAL '1 day', 'DELIVERED'),
  ('82000000-0000-0000-0000-000000000012', '81000000-0000-0000-0000-000000000015', NOW() - INTERVAL '3 days', 'DELIVERED'),
  ('82000000-0000-0000-0000-000000000013', '81000000-0000-0000-0000-000000000016', NOW() - INTERVAL '7 days', 'DELIVERED'),
  ('82000000-0000-0000-0000-000000000014', '81000000-0000-0000-0000-000000000017', NOW() - INTERVAL '1 day', 'DELIVERED'),
  ('82000000-0000-0000-0000-000000000015', '81000000-0000-0000-0000-000000000018', NOW() - INTERVAL '12 hours', 'DELIVERED'),
  
  -- Pending notifications
  ('82000000-0000-0000-0000-000000000016', '81000000-0000-0000-0000-000000000006', NOW() - INTERVAL '1 hour', 'PENDING'),
  ('82000000-0000-0000-0000-000000000017', '81000000-0000-0000-0000-000000000007', NOW() - INTERVAL '30 minutes', 'PENDING'),
  
  -- Failed notification with retry attempts
  ('82000000-0000-0000-0000-000000000018', '81000000-0000-0000-0000-000000000008', NOW() - INTERVAL '2 days', 'FAILED'),
  ('82000000-0000-0000-0000-000000000019', '81000000-0000-0000-0000-000000000008', NOW() - INTERVAL '2 days' + INTERVAL '5 minutes', 'FAILED'),
  ('82000000-0000-0000-0000-000000000020', '81000000-0000-0000-0000-000000000008', NOW() - INTERVAL '2 days' + INTERVAL '10 minutes', 'FAILED')
ON CONFLICT (id) DO NOTHING;