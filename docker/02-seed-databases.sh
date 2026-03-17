#!/bin/sh
# ============================================================
# AssureFlow - Database Seed Script
# ============================================================
# Compatible Windows (Git Bash) & Linux
# ============================================================

set -e

echo "============================================================"
echo "Starting database seeding with AssureFlow mock data..."
echo "============================================================"

# Wait for databases to be fully ready
sleep 3

echo ">> Seeding Policy database..."
if [ -f "/seed-data/policy.sql" ]; then
    psql -v ON_ERROR_STOP=1 -U "$POSTGRES_USER" -d policy_db -f /seed-data/policy.sql || echo "Policy seed skipped"
fi

echo ">> Seeding Client database..."
if [ -f "/seed-data/client.sql" ]; then
    psql -v ON_ERROR_STOP=1 -U "$POSTGRES_USER" -d client_db -f /seed-data/client.sql || echo "Client seed skipped"
fi

echo ">> Seeding Billing database..."
if [ -f "/seed-data/billing.sql" ]; then
    psql -v ON_ERROR_STOP=1 -U "$POSTGRES_USER" -d billing_db -f /seed-data/billing.sql || echo "Billing seed skipped"
fi

echo ">> Seeding Claims database..."
if [ -f "/seed-data/claims.sql" ]; then
    psql -v ON_ERROR_STOP=1 -U "$POSTGRES_USER" -d claims_db -f /seed-data/claims.sql || echo "Claims seed skipped"
fi

echo ">> Seeding Notification database..."
if [ -f "/seed-data/notification.sql" ]; then
    psql -v ON_ERROR_STOP=1 -U "$POSTGRES_USER" -d notification_db -f /seed-data/notification.sql || echo "Notification seed skipped"
fi

echo ">> Seeding IAM database..."
if [ -f "/seed-data/iam.sql" ]; then
    psql -v ON_ERROR_STOP=1 -U "$POSTGRES_USER" -d iam_db -f /seed-data/iam.sql || echo "IAM seed skipped"
fi

echo ">> Seeding Workflow database..."
if [ -f "/seed-data/workflow.sql" ]; then
    psql -v ON_ERROR_STOP=1 -U "$POSTGRES_USER" -d workflow_db -f /seed-data/workflow.sql || echo "Workflow seed skipped"
fi

echo "============================================================"
echo "Database seeding completed successfully."
echo "============================================================"
