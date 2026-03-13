-- ============================================================
-- Billing Service - Fake Data
-- Invoices, Payments, and Billing Events
-- ============================================================

-- Insert Invoices
INSERT INTO invoices (id, invoice_number, policy_id, client_id, amount, due_date, status, created_at, updated_at) VALUES
  ('iiiiiiii-0000-0000-0000-000000000001', 'INV-2024-001', 'pppppppp-0000-0000-0000-000000000001', 'cccccccc-0000-0000-0000-000000000001', 1200.00, '2024-01-31', 'PAID',    NOW() - INTERVAL '30 days', NOW() - INTERVAL '25 days'),
  ('iiiiiiii-0000-0000-0000-000000000002', 'INV-2024-002', 'pppppppp-0000-0000-0000-000000000002', 'cccccccc-0000-0000-0000-000000000001', 800.00,  '2024-02-28', 'PAID',    NOW() - INTERVAL '25 days', NOW() - INTERVAL '20 days'),
  ('iiiiiiii-0000-0000-0000-000000000003', 'INV-2024-003', 'pppppppp-0000-0000-0000-000000000003', 'cccccccc-0000-0000-0000-000000000002', 2400.00, '2024-02-15', 'PAID',    NOW() - INTERVAL '20 days', NOW() - INTERVAL '15 days'),
  ('iiiiiiii-0000-0000-0000-000000000004', 'INV-2024-004', 'pppppppp-0000-0000-0000-000000000004', 'cccccccc-0000-0000-0000-000000000002', 1800.00, '2024-03-31', 'PENDING', NOW() - INTERVAL '15 days', NOW() - INTERVAL '15 days'),
  ('iiiiiiii-0000-0000-0000-000000000005', 'INV-2024-005', 'pppppppp-0000-0000-0000-000000000005', 'cccccccc-0000-0000-0000-000000000003', 1500.00, '2024-02-29', 'OVERDUE', NOW() - INTERVAL '10 days', NOW() - INTERVAL '10 days'),
  ('iiiiiiii-0000-0000-0000-000000000006', 'INV-2024-006', 'pppppppp-0000-0000-0000-000000000006', 'cccccccc-0000-0000-0000-000000000004', 300.00,  '2024-04-30', 'DRAFT',   NOW() - INTERVAL '5 days', NOW() - INTERVAL '5 days')
ON CONFLICT (id) DO NOTHING;

-- Insert Payments
INSERT INTO payments (id, payment_number, invoice_id, amount, payment_date, payment_method, status, transaction_id, created_at, updated_at) VALUES
  ('yyyyyyyy-0000-0000-0000-000000000001', 'PAY-2024-001', 'iiiiiiii-0000-0000-0000-000000000001', 1200.00, '2024-01-25', 'CREDIT_CARD', 'COMPLETED', 'TXN-CC-001', NOW() - INTERVAL '25 days', NOW() - INTERVAL '25 days'),
  ('yyyyyyyy-0000-0000-0000-000000000002', 'PAY-2024-002', 'iiiiiiii-0000-0000-0000-000000000002', 800.00,  '2024-02-20', 'BANK_TRANSFER', 'COMPLETED', 'TXN-BT-002', NOW() - INTERVAL '20 days', NOW() - INTERVAL '20 days'),
  ('yyyyyyyy-0000-0000-0000-000000000003', 'PAY-2024-003', 'iiiiiiii-0000-0000-0000-000000000003', 2400.00, '2024-02-10', 'CREDIT_CARD', 'COMPLETED', 'TXN-CC-003', NOW() - INTERVAL '15 days', NOW() - INTERVAL '15 days'),
  ('yyyyyyyy-0000-0000-0000-000000000004', 'PAY-2024-004', 'iiiiiiii-0000-0000-0000-000000000005', 750.00,  '2024-03-05', 'CASH', 'COMPLETED', 'TXN-CASH-004', NOW() - INTERVAL '8 days', NOW() - INTERVAL '8 days')
ON CONFLICT (id) DO NOTHING;

-- Insert Billing Events
INSERT INTO billing_events (id, invoice_id, event_type, event_data, created_at) VALUES
  ('bbbbbbbb-0000-0000-0000-000000000001', 'iiiiiiii-0000-0000-0000-000000000001', 'INVOICE_GENERATED', '{"amount": 1200.00, "due_date": "2024-01-31"}', NOW() - INTERVAL '30 days'),
  ('bbbbbbbb-0000-0000-0000-000000000002', 'iiiiiiii-0000-0000-0000-000000000001', 'PAYMENT_RECEIVED', '{"amount": 1200.00, "method": "CREDIT_CARD"}', NOW() - INTERVAL '25 days'),
  ('bbbbbbbb-0000-0000-0000-000000000003', 'iiiiiiii-0000-0000-0000-000000000005', 'INVOICE_OVERDUE', '{"days_overdue": 15, "amount": 1500.00}', NOW() - INTERVAL '5 days'),
  ('bbbbbbbb-0000-0000-0000-000000000004', 'iiiiiiii-0000-0000-0000-000000000005', 'REMINDER_SENT', '{"reminder_type": "EMAIL", "recipient": "youssef.nejjari@gmail.com"}', NOW() - INTERVAL '3 days')
ON CONFLICT (id) DO NOTHING;

-- Insert Payment Reminders
INSERT INTO payment_reminders (id, invoice_id, reminder_type, sent_date, status, next_reminder_date) VALUES
  ('mmmmmmmm-0000-0000-0000-000000000001', 'iiiiiiii-0000-0000-0000-000000000005', 'FIRST_REMINDER', NOW() - INTERVAL '10 days', 'SENT', NOW() + INTERVAL '5 days'),
  ('mmmmmmmm-0000-0000-0000-000000000002', 'iiiiiiii-0000-0000-0000-000000000005', 'SECOND_REMINDER', NOW() - INTERVAL '3 days', 'SENT', NOW() + INTERVAL '7 days'),
  ('mmmmmmmm-0000-0000-0000-000000000003', 'iiiiiiii-0000-0000-0000-000000000004', 'COURTESY_REMINDER', NOW() - INTERVAL '5 days', 'SENT', NOW() + INTERVAL '10 days')
ON CONFLICT (id) DO NOTHING;