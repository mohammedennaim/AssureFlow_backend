-- ============================================================
-- Notification Service - Fake Data
-- NotificationEntity: UUID id/policyId, has createdAt (@PrePersist)
-- NotificationType enum: POLICY_CREATED, INVOICE_GENERATED,
--                         CLAIM_SUBMITTED, CLAIM_APPROVED, PAYMENT_RECEIVED
-- ============================================================

INSERT INTO notifications (id, type, channel, recipient, link_name, subject, content, status, policy_id, sent_at, created_at) VALUES
  ('88888888-0000-0000-0000-000000000001', 'POLICY_CREATED',    'EMAIL', 'ahmed.benali@gmail.com', 'Voir votre police',   'Votre police d assurance est active',   'Bonjour Ahmed, votre police HEALTH POL-2024-0001 est maintenant active.',         'SENT', 'dddddddd-0000-0000-0000-000000000001', NOW(), NOW()),
  ('88888888-0000-0000-0000-000000000002', 'INVOICE_GENERATED', 'EMAIL', 'ahmed.benali@gmail.com', 'Voir la facture',     'Nouvelle facture INV-2024-0001',        'Bonjour Ahmed, votre facture de 600.00 DH est disponible.',                       'SENT', 'dddddddd-0000-0000-0000-000000000001', NOW(), NOW()),
  ('88888888-0000-0000-0000-000000000003', 'PAYMENT_RECEIVED',  'EMAIL', 'ahmed.benali@gmail.com', 'Voir le recu',        'Paiement recu - Merci',                 'Bonjour Ahmed, nous avons bien recu votre paiement de 600.00 DH.',                'SENT', 'dddddddd-0000-0000-0000-000000000001', NOW(), NOW()),
  ('88888888-0000-0000-0000-000000000004', 'CLAIM_SUBMITTED',   'EMAIL', 'ahmed.benali@gmail.com', 'Voir la reclamation', 'Reclamation CLM-2024-0001 recue',       'Votre reclamation a bien ete enregistree et est en cours de traitement.',          'SENT', 'dddddddd-0000-0000-0000-000000000001', NOW(), NOW()),
  ('88888888-0000-0000-0000-000000000005', 'CLAIM_APPROVED',    'EMAIL', 'ahmed.benali@gmail.com', 'Voir la decision',    'Reclamation CLM-2024-0001 approuvee',   'Bonne nouvelle! Votre reclamation a ete approuvee pour un montant de 3200.00 DH.','SENT', 'dddddddd-0000-0000-0000-000000000001', NOW(), NOW()),
  ('88888888-0000-0000-0000-000000000006', 'POLICY_CREATED',    'EMAIL', 'fatima.alami@gmail.com', 'Voir votre police',   'Votre police d assurance est active',   'Bonjour Fatima, votre police LIFE POL-2024-0003 est maintenant active.',           'SENT', 'dddddddd-0000-0000-0000-000000000003', NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
