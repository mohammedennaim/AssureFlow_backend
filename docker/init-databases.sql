-- ============================================================
-- AssureFlow - Database Initialization Script
-- Replaces init-databases.sh with pure SQL
-- Executed once at first Docker volume startup
-- ============================================================

-- Create policy_db
SELECT 'CREATE DATABASE policy_db' WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'policy_db')\gexec
GRANT ALL PRIVILEGES ON DATABASE policy_db TO assureflow;

-- Create client_db
SELECT 'CREATE DATABASE client_db' WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'client_db')\gexec
GRANT ALL PRIVILEGES ON DATABASE client_db TO assureflow;

-- Create billing_db
SELECT 'CREATE DATABASE billing_db' WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'billing_db')\gexec
GRANT ALL PRIVILEGES ON DATABASE billing_db TO assureflow;

-- Create claims_db
SELECT 'CREATE DATABASE claims_db' WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'claims_db')\gexec
GRANT ALL PRIVILEGES ON DATABASE claims_db TO assureflow;

-- Create notification_db
SELECT 'CREATE DATABASE notification_db' WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'notification_db')\gexec
GRANT ALL PRIVILEGES ON DATABASE notification_db TO assureflow;

-- Create iam_db
SELECT 'CREATE DATABASE iam_db' WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'iam_db')\gexec
GRANT ALL PRIVILEGES ON DATABASE iam_db TO assureflow;

-- Create workflow_db
SELECT 'CREATE DATABASE workflow_db' WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'workflow_db')\gexec
GRANT ALL PRIVILEGES ON DATABASE workflow_db TO assureflow;
