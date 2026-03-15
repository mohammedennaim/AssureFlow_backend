-- ============================================================
-- AssureFlow - Database Initialization Script
-- Executed once at first Docker volume startup
-- ============================================================

-- Create policy_db
CREATE DATABASE policy_db OWNER assureflow;

-- Create client_db
CREATE DATABASE client_db OWNER assureflow;

-- Create billing_db
CREATE DATABASE billing_db OWNER assureflow;

-- Create claims_db
CREATE DATABASE claims_db OWNER assureflow;

-- Create notification_db
CREATE DATABASE notification_db OWNER assureflow;

-- Create iam_db
CREATE DATABASE iam_db OWNER assureflow;

-- Create workflow_db
CREATE DATABASE workflow_db OWNER assureflow;
