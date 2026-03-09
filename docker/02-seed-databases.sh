#!/bin/bash
set -e

echo "============================================================"
echo "🌱 Starting database seeding with AssureFlow mock data..."
echo "============================================================"

# Wait a brief moment to ensure DBs are fully ready (if needed)
sleep 2

if [ -f "/seed-data/policy.sql" ]; then
    echo ">> Seeding Policy database..."
    psql -v ON_ERROR_STOP=1 -U "$POSTGRES_USER" -d policy_db -f /seed-data/policy.sql
fi

if [ -f "/seed-data/client.sql" ]; then
    echo ">> Seeding Client database..."
    psql -v ON_ERROR_STOP=1 -U "$POSTGRES_USER" -d client_db -f /seed-data/client.sql
fi

if [ -f "/seed-data/billing.sql" ]; then
    echo ">> Seeding Billing database..."
    psql -v ON_ERROR_STOP=1 -U "$POSTGRES_USER" -d billing_db -f /seed-data/billing.sql
fi

if [ -f "/seed-data/claims.sql" ]; then
    echo ">> Seeding Claims database..."
    psql -v ON_ERROR_STOP=1 -U "$POSTGRES_USER" -d claims_db -f /seed-data/claims.sql
fi

if [ -f "/seed-data/notification.sql" ]; then
    echo ">> Seeding Notification database..."
    psql -v ON_ERROR_STOP=1 -U "$POSTGRES_USER" -d notification_db -f /seed-data/notification.sql
fi

if [ -f "/seed-data/iam.sql" ]; then
    echo ">> Seeding IAM database..."
    psql -v ON_ERROR_STOP=1 -U "$POSTGRES_USER" -d iam_db -f /seed-data/iam.sql
fi

if [ -f "/seed-data/workflow.sql" ]; then
    echo ">> Seeding Workflow database..."
    psql -v ON_ERROR_STOP=1 -U "$POSTGRES_USER" -d workflow_db -f /seed-data/workflow.sql
fi

echo "============================================================"
echo "✅ Database seeding completed successfully."
echo "============================================================"
