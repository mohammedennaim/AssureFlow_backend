-- ============================================================
-- IAM Service - Seed data aligned with current entities
-- ============================================================

INSERT INTO roles (id, name, description) VALUES
  ('10000000-0000-0000-0000-000000000001', 'ADMIN', 'System administrator'),
  ('10000000-0000-0000-0000-000000000002', 'AGENT', 'Insurance agent'),
  ('10000000-0000-0000-0000-000000000003', 'CLIENT', 'Client role'),
  ('10000000-0000-0000-0000-000000000004', 'FINANCE', 'Finance role')
ON CONFLICT (id) DO NOTHING;

INSERT INTO users (id, username, email, password_hash, active, role_id) VALUES
  ('20000000-0000-0000-0000-000000000001', 'admin', 'admin@assureflow.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', true, '10000000-0000-0000-0000-000000000001'),
  ('20000000-0000-0000-0000-000000000002', 'agent.hassan', 'hassan.agent@assureflow.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', true, '10000000-0000-0000-0000-000000000002'),
  ('20000000-0000-0000-0000-000000000003', 'finance.omar', 'omar.finance@assureflow.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', true, '10000000-0000-0000-0000-000000000004')
ON CONFLICT (id) DO NOTHING;