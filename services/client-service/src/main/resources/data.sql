-- ============================================================
-- Client Service - Comprehensive Seed Data for Testing
-- ClientEntity: UUID id, columns use camelCase -> snake_case by Hibernate
-- AddressEntity: has is_primary column (default false)
-- ============================================================

-- Clients
INSERT INTO clients (id, client_number, first_name, last_name, email, phone, date_of_birth, cin, status, type, user_id, active, created_at, updated_at) VALUES
  -- Active Individual Clients
  ('cccccccc-0000-0000-0000-000000000001', 'CLI-2024-001', 'Ahmed',    'Benali',    'ahmed.benali@gmail.com',     '0661234567', '1985-03-15', 'BK123456', 'ACTIVE', 'INDIVIDUAL', '20000000-0000-0000-0000-000000000011', true,  NOW(), NOW()),
  ('cccccccc-0000-0000-0000-000000000002', 'CLI-2024-002', 'Fatima',   'Alami',     'fatima.alami@gmail.com',     '0662345678', '1990-07-22', 'CB234567', 'ACTIVE', 'INDIVIDUAL', '20000000-0000-0000-0000-000000000012', true,  NOW(), NOW()),
  ('cccccccc-0000-0000-0000-000000000003', 'CLI-2024-003', 'Youssef',  'Nejjari',   'youssef.nejjari@gmail.com',  '0663456789', '1978-11-05', 'GC345678', 'ACTIVE', 'INDIVIDUAL', '20000000-0000-0000-0000-000000000013', true,  NOW(), NOW()),
  ('cccccccc-0000-0000-0000-000000000004', 'CLI-2024-004', 'Leila',    'Tahiri',    'leila.tahiri@gmail.com',     '0664567890', '1995-01-30', 'JE456789', 'ACTIVE', 'INDIVIDUAL', '20000000-0000-0000-0000-000000000014', true,  NOW(), NOW()),
  ('cccccccc-0000-0000-0000-000000000005', 'CLI-2024-005', 'Hassan',   'Zouiten',   'hassan.zouiten@gmail.com',   '0665678901', '1972-09-14', 'LC567890', 'INACTIVE', 'INDIVIDUAL', '20000000-0000-0000-0000-000000000015', false, NOW(), NOW()),
  ('cccccccc-0000-0000-0000-000000000006', 'CLI-2024-006', 'Nadia',    'Benjelloun','nadia.benjelloun@gmail.com',  '0666789012', '1988-05-18', 'MD678901', 'ACTIVE', 'INDIVIDUAL', NULL, true,  NOW(), NOW()),
  ('cccccccc-0000-0000-0000-000000000007', 'CLI-2024-007', 'Karim',    'El Fassi',  'karim.elfassi@gmail.com',    '0667890123', '1982-12-03', 'NE789012', 'ACTIVE', 'INDIVIDUAL', NULL, true,  NOW(), NOW()),
  ('cccccccc-0000-0000-0000-000000000008', 'CLI-2024-008', 'Samira',   'Idrissi',   'samira.idrissi@gmail.com',   '0668901234', '1993-08-25', 'OF890123', 'ACTIVE', 'INDIVIDUAL', NULL, true,  NOW(), NOW()),
  ('cccccccc-0000-0000-0000-000000000009', 'CLI-2024-009', 'Mehdi',    'Lahlou',    'mehdi.lahlou@gmail.com',     '0669012345', '1975-04-12', 'PG901234', 'PENDING', 'INDIVIDUAL', NULL, true,  NOW(), NOW()),
  ('cccccccc-0000-0000-0000-000000000010', 'CLI-2024-010', 'Zineb',    'Amrani',    'zineb.amrani@gmail.com',     '0660123456', '1991-10-07', 'QH012345', 'ACTIVE', 'INDIVIDUAL', NULL, true,  NOW(), NOW()),
  
  -- Business Clients
  ('cccccccc-0000-0000-0000-000000000011', 'CLI-2024-011', 'TechCorp', 'SARL',      'contact@techcorp.ma',        '0522334455', NULL,         'RC123456', 'ACTIVE', 'BUSINESS', NULL, true,  NOW(), NOW()),
  ('cccccccc-0000-0000-0000-000000000012', 'CLI-2024-012', 'AutoFleet','SA',        'info@autofleet.ma',          '0522445566', NULL,         'RC234567', 'ACTIVE', 'BUSINESS', NULL, true,  NOW(), NOW()),
  ('cccccccc-0000-0000-0000-000000000013', 'CLI-2024-013', 'MediCare', 'Clinic',    'admin@medicare.ma',          '0522556677', NULL,         'RC345678', 'ACTIVE', 'BUSINESS', NULL, true,  NOW(), NOW()),
  
  -- Suspended/Inactive Clients
  ('cccccccc-0000-0000-0000-000000000014', 'CLI-2024-014', 'Omar',     'Tazi',      'omar.tazi@gmail.com',        '0661111111', '1980-02-20', 'RI456789', 'SUSPENDED', 'INDIVIDUAL', NULL, false, NOW(), NOW()),
  ('cccccccc-0000-0000-0000-000000000015', 'CLI-2024-015', 'Salma',    'Berrada',   'salma.berrada@gmail.com',    '0662222222', '1987-06-15', 'SJ567890', 'INACTIVE', 'INDIVIDUAL', NULL, false, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- Addresses (has is_primary column, postal_code is camelCase mapped)
INSERT INTO addresses (id, client_id, street, city, postal_code, country, is_primary) VALUES
  -- Primary addresses
  ('dddddddd-0000-0000-0000-000000000001', 'cccccccc-0000-0000-0000-000000000001', '12 Rue Hassan II',           'Casablanca',  '20000', 'Maroc', true),
  ('dddddddd-0000-0000-0000-000000000002', 'cccccccc-0000-0000-0000-000000000002', '45 Boulevard Mohammed V',    'Rabat',       '10000', 'Maroc', true),
  ('dddddddd-0000-0000-0000-000000000003', 'cccccccc-0000-0000-0000-000000000003', '8 Avenue des FAR',           'Marrakech',   '40000', 'Maroc', true),
  ('dddddddd-0000-0000-0000-000000000004', 'cccccccc-0000-0000-0000-000000000004', '23 Rue Ibn Sina',            'Fes',         '30000', 'Maroc', true),
  ('dddddddd-0000-0000-0000-000000000005', 'cccccccc-0000-0000-0000-000000000005', '67 Rue du Palmier',          'Agadir',      '80000', 'Maroc', true),
  ('dddddddd-0000-0000-0000-000000000006', 'cccccccc-0000-0000-0000-000000000006', '34 Avenue Moulay Youssef',   'Tanger',      '90000', 'Maroc', true),
  ('dddddddd-0000-0000-0000-000000000007', 'cccccccc-0000-0000-0000-000000000007', '56 Rue de la Liberté',       'Casablanca',  '20100', 'Maroc', true),
  ('dddddddd-0000-0000-0000-000000000008', 'cccccccc-0000-0000-0000-000000000008', '89 Boulevard Zerktouni',     'Casablanca',  '20300', 'Maroc', true),
  ('dddddddd-0000-0000-0000-000000000009', 'cccccccc-0000-0000-0000-000000000009', '12 Rue Allal Ben Abdellah',  'Rabat',       '10030', 'Maroc', true),
  ('dddddddd-0000-0000-0000-000000000010', 'cccccccc-0000-0000-0000-000000000010', '78 Avenue Hassan II',        'Kenitra',     '14000', 'Maroc', true),
  ('dddddddd-0000-0000-0000-000000000011', 'cccccccc-0000-0000-0000-000000000011', '123 Zone Industrielle',      'Casablanca',  '20250', 'Maroc', true),
  ('dddddddd-0000-0000-0000-000000000012', 'cccccccc-0000-0000-0000-000000000012', '45 Quartier Industriel',     'Tanger',      '90100', 'Maroc', true),
  ('dddddddd-0000-0000-0000-000000000013', 'cccccccc-0000-0000-0000-000000000013', '67 Avenue Moulay Rachid',    'Rabat',       '10050', 'Maroc', true),
  ('dddddddd-0000-0000-0000-000000000014', 'cccccccc-0000-0000-0000-000000000014', '90 Rue Abdelmoumen',         'Casablanca',  '20400', 'Maroc', true),
  ('dddddddd-0000-0000-0000-000000000015', 'cccccccc-0000-0000-0000-000000000015', '11 Boulevard Anfa',          'Casablanca',  '20050', 'Maroc', true),
  
  -- Secondary addresses for some clients
  ('dddddddd-0000-0000-0000-000000000016', 'cccccccc-0000-0000-0000-000000000001', '5 Résidence Al Amal',        'Rabat',       '10020', 'Maroc', false),
  ('dddddddd-0000-0000-0000-000000000017', 'cccccccc-0000-0000-0000-000000000003', '22 Villa des Orangers',      'Casablanca',  '20200', 'Maroc', false),
  ('dddddddd-0000-0000-0000-000000000018', 'cccccccc-0000-0000-0000-000000000011', '88 Succursale Nord',         'Rabat',       '10000', 'Maroc', false)
ON CONFLICT (id) DO NOTHING;
