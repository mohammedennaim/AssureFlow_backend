-- ============================================================
-- Client Service - Fake Data
-- ClientEntity: UUID id, columns use camelCase -> snake_case by Hibernate
-- AddressEntity: has is_primary column (default false)
-- ============================================================

-- Clients
INSERT INTO clients (id, client_number, first_name, last_name, email, phone, date_of_birth, cin, status, type, user_id, active, created_at, updated_at) VALUES
  ('cccccccc-0000-0000-0000-000000000001', 'CLI-2024-001', 'Ahmed',   'Benali', 'ahmed.benali@gmail.com',    '0661234567', '1985-03-15', 'BK123456', 'ACTIVE',   'INDIVIDUAL', 'aaaaaaaa-0000-0000-0000-000000000004', true, NOW(), NOW()),
  ('cccccccc-0000-0000-0000-000000000002', 'CLI-2024-002', 'Fatima',  'Alami',  'fatima.alami@gmail.com',    '0662345678', '1990-07-22', 'CB234567', 'ACTIVE',   'INDIVIDUAL', 'aaaaaaaa-0000-0000-0000-000000000005', true, NOW(), NOW()),
  ('cccccccc-0000-0000-0000-000000000003', 'CLI-2024-003', 'Youssef', 'Nejjari','youssef.nejjari@gmail.com', '0663456789', '1978-11-05', 'GC345678', 'ACTIVE',   'INDIVIDUAL', NULL,                                   true, NOW(), NOW()),
  ('cccccccc-0000-0000-0000-000000000004', 'CLI-2024-004', 'Leila',   'Tahiri', 'leila.tahiri@gmail.com',    '0664567890', '1995-01-30', 'JE456789', 'ACTIVE',   'INDIVIDUAL', NULL,                                   true, NOW(), NOW()),
  ('cccccccc-0000-0000-0000-000000000005', 'CLI-2024-005', 'Hassan',  'Zouiten','hassan.zouiten@gmail.com',  '0665678901', '1972-09-14', 'LC567890', 'INACTIVE', 'INDIVIDUAL', NULL,                                   false, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- Addresses (has is_primary column, postal_code is camelCase mapped)
INSERT INTO addresses (id, client_id, street, city, postal_code, country, is_primary) VALUES
  ('dddddddd-0000-0000-0000-000000000001', 'cccccccc-0000-0000-0000-000000000001', '12 Rue Hassan II',       'Casablanca', '20000', 'Maroc', true),
  ('dddddddd-0000-0000-0000-000000000002', 'cccccccc-0000-0000-0000-000000000002', '45 Boulevard Mohammed V','Rabat',      '10000', 'Maroc', true),
  ('dddddddd-0000-0000-0000-000000000003', 'cccccccc-0000-0000-0000-000000000003', '8 Avenue des FAR',       'Marrakech',  '40000', 'Maroc', true),
  ('dddddddd-0000-0000-0000-000000000004', 'cccccccc-0000-0000-0000-000000000004', '23 Rue Ibn Sina',        'Fes',        '30000', 'Maroc', true),
  ('dddddddd-0000-0000-0000-000000000005', 'cccccccc-0000-0000-0000-000000000005', '67 Rue du Palmier',      'Agadir',     '80000', 'Maroc', true)
ON CONFLICT (id) DO NOTHING;
