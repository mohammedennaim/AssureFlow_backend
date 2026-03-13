-- ============================================================
-- IAM Service - Fake Data
-- Users, Roles, and Permissions for testing
-- ============================================================

-- Insert Roles
INSERT INTO roles (id, name, description, created_at, updated_at) VALUES
  ('rrrrrrrr-0000-0000-0000-000000000001', 'ADMIN',  'System Administrator with full access', NOW(), NOW()),
  ('rrrrrrrr-0000-0000-0000-000000000002', 'AGENT',  'Insurance Agent with policy management access', NOW(), NOW()),
  ('rrrrrrrr-0000-0000-0000-000000000003', 'CLIENT', 'Insurance Client with limited access', NOW(), NOW()),
  ('rrrrrrrr-0000-0000-0000-000000000004', 'FINANCE','Finance team with billing access', NOW(), NOW()),
  ('rrrrrrrr-0000-0000-0000-000000000005', 'OPS',    'Operations team with claims access', NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- Insert Users (password is 'password123' encoded with BCrypt)
INSERT INTO users (id, username, email, password, first_name, last_name, phone, active, email_verified, created_at, updated_at) VALUES
  ('aaaaaaaa-0000-0000-0000-000000000001', 'admin',        'admin@assureflow.com',        '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'System',  'Administrator', '0661111111', true, true, NOW(), NOW()),
  ('aaaaaaaa-0000-0000-0000-000000000002', 'agent.hassan', 'hassan.agent@assureflow.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'Hassan',  'Alami',         '0662222222', true, true, NOW(), NOW()),
  ('aaaaaaaa-0000-0000-0000-000000000003', 'agent.sara',   'sara.agent@assureflow.com',   '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'Sara',    'Benali',        '0663333333', true, true, NOW(), NOW()),
  ('aaaaaaaa-0000-0000-0000-000000000004', 'ahmed.client', 'ahmed.benali@gmail.com',      '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'Ahmed',   'Benali',        '0661234567', true, true, NOW(), NOW()),
  ('aaaaaaaa-0000-0000-0000-000000000005', 'fatima.client','fatima.alami@gmail.com',      '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'Fatima',  'Alami',         '0662345678', true, true, NOW(), NOW()),
  ('aaaaaaaa-0000-0000-0000-000000000006', 'finance.omar', 'omar.finance@assureflow.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'Omar',    'Tazi',          '0664444444', true, true, NOW(), NOW()),
  ('aaaaaaaa-0000-0000-0000-000000000007', 'ops.leila',    'leila.ops@assureflow.com',    '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'Leila',   'Nejjari',       '0665555555', true, true, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- Insert User-Role associations
INSERT INTO user_roles (user_id, role_id) VALUES
  ('aaaaaaaa-0000-0000-0000-000000000001', 'rrrrrrrr-0000-0000-0000-000000000001'), -- admin -> ADMIN
  ('aaaaaaaa-0000-0000-0000-000000000002', 'rrrrrrrr-0000-0000-0000-000000000002'), -- hassan -> AGENT
  ('aaaaaaaa-0000-0000-0000-000000000003', 'rrrrrrrr-0000-0000-0000-000000000002'), -- sara -> AGENT
  ('aaaaaaaa-0000-0000-0000-000000000004', 'rrrrrrrr-0000-0000-0000-000000000003'), -- ahmed -> CLIENT
  ('aaaaaaaa-0000-0000-0000-000000000005', 'rrrrrrrr-0000-0000-0000-000000000003'), -- fatima -> CLIENT
  ('aaaaaaaa-0000-0000-0000-000000000006', 'rrrrrrrr-0000-0000-0000-000000000004'), -- omar -> FINANCE
  ('aaaaaaaa-0000-0000-0000-000000000007', 'rrrrrrrr-0000-0000-0000-000000000005')  -- leila -> OPS
ON CONFLICT (user_id, role_id) DO NOTHING;

-- Insert Audit Logs
INSERT INTO audit_logs (id, user_id, action, resource_type, resource_id, details, ip_address, user_agent, created_at) VALUES
  ('llllllll-0000-0000-0000-000000000001', 'aaaaaaaa-0000-0000-0000-000000000001', 'LOGIN',  'USER', 'aaaaaaaa-0000-0000-0000-000000000001', 'Admin login successful', '127.0.0.1', 'Mozilla/5.0', NOW() - INTERVAL '1 day'),
  ('llllllll-0000-0000-0000-000000000002', 'aaaaaaaa-0000-0000-0000-000000000002', 'LOGIN',  'USER', 'aaaaaaaa-0000-0000-0000-000000000002', 'Agent login successful', '127.0.0.1', 'Mozilla/5.0', NOW() - INTERVAL '2 hours'),
  ('llllllll-0000-0000-0000-000000000003', 'aaaaaaaa-0000-0000-0000-000000000004', 'LOGIN',  'USER', 'aaaaaaaa-0000-0000-0000-000000000004', 'Client login successful', '127.0.0.1', 'Mozilla/5.0', NOW() - INTERVAL '30 minutes')
ON CONFLICT (id) DO NOTHING;