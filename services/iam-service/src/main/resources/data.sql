-- ============================================================
-- IAM Service - Fake Data
-- Replaces DataSeeder.java: seeds permissions, roles, role_permissions, users
-- Password 'Admin@123456' bcrypt hash
-- ============================================================

-- Permissions (resource + action, String id)
INSERT INTO permissions (id, resource, action) VALUES
  ('eeeeeeee-0000-0000-0000-000000000001', 'users',    'read'),
  ('eeeeeeee-0000-0000-0000-000000000002', 'users',    'write'),
  ('eeeeeeee-0000-0000-0000-000000000003', 'users',    'delete'),
  ('eeeeeeee-0000-0000-0000-000000000004', 'policies', 'read'),
  ('eeeeeeee-0000-0000-0000-000000000005', 'policies', 'write'),
  ('eeeeeeee-0000-0000-0000-000000000006', 'policies', 'approve'),
  ('eeeeeeee-0000-0000-0000-000000000007', 'claims',   'read'),
  ('eeeeeeee-0000-0000-0000-000000000008', 'claims',   'write'),
  ('eeeeeeee-0000-0000-0000-000000000009', 'claims',   'approve'),
  ('eeeeeeee-0000-0000-0000-000000000010', 'billing',  'read'),
  ('eeeeeeee-0000-0000-0000-000000000011', 'billing',  'write')
ON CONFLICT DO NOTHING;

-- Roles
INSERT INTO roles (id, name) VALUES
  ('11111111-0000-0000-0000-000000000001', 'ADMIN'),
  ('11111111-0000-0000-0000-000000000002', 'AGENT'),
  ('11111111-0000-0000-0000-000000000003', 'CLIENT'),
  ('11111111-0000-0000-0000-000000000004', 'FINANCE')
ON CONFLICT (id) DO NOTHING;

-- Role-Permission assignments (ADMIN gets all permissions)
INSERT INTO role_permissions (role_id, permission_id) VALUES
  ('11111111-0000-0000-0000-000000000001', 'eeeeeeee-0000-0000-0000-000000000001'),
  ('11111111-0000-0000-0000-000000000001', 'eeeeeeee-0000-0000-0000-000000000002'),
  ('11111111-0000-0000-0000-000000000001', 'eeeeeeee-0000-0000-0000-000000000003'),
  ('11111111-0000-0000-0000-000000000001', 'eeeeeeee-0000-0000-0000-000000000004'),
  ('11111111-0000-0000-0000-000000000001', 'eeeeeeee-0000-0000-0000-000000000005'),
  ('11111111-0000-0000-0000-000000000001', 'eeeeeeee-0000-0000-0000-000000000006'),
  ('11111111-0000-0000-0000-000000000001', 'eeeeeeee-0000-0000-0000-000000000007'),
  ('11111111-0000-0000-0000-000000000001', 'eeeeeeee-0000-0000-0000-000000000008'),
  ('11111111-0000-0000-0000-000000000001', 'eeeeeeee-0000-0000-0000-000000000009'),
  ('11111111-0000-0000-0000-000000000001', 'eeeeeeee-0000-0000-0000-000000000010'),
  ('11111111-0000-0000-0000-000000000001', 'eeeeeeee-0000-0000-0000-000000000011'),
  -- AGENT gets policies + claims + billing read
  ('11111111-0000-0000-0000-000000000002', 'eeeeeeee-0000-0000-0000-000000000004'),
  ('11111111-0000-0000-0000-000000000002', 'eeeeeeee-0000-0000-0000-000000000005'),
  ('11111111-0000-0000-0000-000000000002', 'eeeeeeee-0000-0000-0000-000000000007'),
  ('11111111-0000-0000-0000-000000000002', 'eeeeeeee-0000-0000-0000-000000000008'),
  ('11111111-0000-0000-0000-000000000002', 'eeeeeeee-0000-0000-0000-000000000010'),
  -- CLIENT gets read only
  ('11111111-0000-0000-0000-000000000003', 'eeeeeeee-0000-0000-0000-000000000004'),
  ('11111111-0000-0000-0000-000000000003', 'eeeeeeee-0000-0000-0000-000000000007'),
  ('11111111-0000-0000-0000-000000000003', 'eeeeeeee-0000-0000-0000-000000000010'),
  -- FINANCE gets billing full + policies read
  ('11111111-0000-0000-0000-000000000004', 'eeeeeeee-0000-0000-0000-000000000004'),
  ('11111111-0000-0000-0000-000000000004', 'eeeeeeee-0000-0000-0000-000000000010'),
  ('11111111-0000-0000-0000-000000000004', 'eeeeeeee-0000-0000-0000-000000000011')
ON CONFLICT DO NOTHING;

-- Users (password = 'Admin@123456')
INSERT INTO users (id, username, email, password_hash, active, role_id, created_at, updated_at) VALUES
  ('aaaaaaaa-0000-0000-0000-000000000001', 'admin',        'admin@assureflow.com',        '$2a$10$XVwBJVDRz1JfYXgU9gS4fOScMQsEJa2Y9e8Xs8UWB/JPLB7lxkDGe', true, '11111111-0000-0000-0000-000000000001', NOW(), NOW()),
  ('aaaaaaaa-0000-0000-0000-000000000002', 'agent.dupont', 'jean.dupont@assureflow.com',  '$2a$10$XVwBJVDRz1JfYXgU9gS4fOScMQsEJa2Y9e8Xs8UWB/JPLB7lxkDGe', true, '11111111-0000-0000-0000-000000000002', NOW(), NOW()),
  ('aaaaaaaa-0000-0000-0000-000000000003', 'agent.martin', 'marie.martin@assureflow.com', '$2a$10$XVwBJVDRz1JfYXgU9gS4fOScMQsEJa2Y9e8Xs8UWB/JPLB7lxkDGe', true, '11111111-0000-0000-0000-000000000002', NOW(), NOW()),
  ('aaaaaaaa-0000-0000-0000-000000000004', 'client.benali','ahmed.benali@gmail.com',      '$2a$10$XVwBJVDRz1JfYXgU9gS4fOScMQsEJa2Y9e8Xs8UWB/JPLB7lxkDGe', true, '11111111-0000-0000-0000-000000000003', NOW(), NOW()),
  ('aaaaaaaa-0000-0000-0000-000000000005', 'client.alami', 'fatima.alami@gmail.com',      '$2a$10$XVwBJVDRz1JfYXgU9gS4fOScMQsEJa2Y9e8Xs8UWB/JPLB7lxkDGe', true, '11111111-0000-0000-0000-000000000003', NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
