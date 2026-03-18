#!/bin/sh
# ============================================================
# AssureFlow - Database Seed Script
# ============================================================
# Executes data.sql files for each service database
# Compatible Windows (Git Bash) & Linux
# ============================================================

set -e

echo "============================================================"
echo "🌱 Starting database seeding with AssureFlow mock data..."
echo "============================================================"

# Wait for PostgreSQL to be fully ready
echo "⏳ Waiting for PostgreSQL to be ready..."

# Use pg_isready to wait for PostgreSQL
max_attempts=30
attempt=0
while ! pg_isready -h localhost -U "$POSTGRES_USER" -d "$POSTGRES_DB" > /dev/null 2>&1; do
    attempt=$((attempt + 1))
    if [ $attempt -ge $max_attempts ]; then
        echo "❌ PostgreSQL is not ready after $max_attempts attempts"
        exit 1
    fi
    echo "   Waiting for PostgreSQL... ($attempt/$max_attempts)"
    sleep 2
done

echo "✅ PostgreSQL is ready!"
echo ""

# Function to execute SQL file
execute_sql() {
    local db_name=$1
    local sql_file=$2
    
    if [ -f "$sql_file" ]; then
        echo ">> Seeding $db_name from $sql_file..."
        # Use Unix socket connection (more reliable in init context)
        PGPASSWORD="$POSTGRES_PASSWORD" psql -U "$POSTGRES_USER" -d "$db_name" -f "$sql_file" 2>&1 || echo "   Warning: $db_name seeding had issues (may already be seeded)"
    else
        echo ">> SQL file not found: $sql_file (skipping $db_name)"
    fi
}

# Execute seed files for each service
echo ""
echo "============================================================"
echo "📊 Executing Seed Data Scripts"
echo "============================================================"
echo ""

execute_sql "policy_db" "/seed-data/policy.sql"
execute_sql "client_db" "/seed-data/client.sql"
execute_sql "billing_db" "/seed-data/billing.sql"
execute_sql "claims_db" "/seed-data/claims.sql"
execute_sql "notification_db" "/seed-data/notification.sql"
execute_sql "iam_db" "/seed-data/iam.sql"
execute_sql "workflow_db" "/seed-data/workflow.sql"

echo ""
echo "============================================================"
echo "✅ Database seeding completed successfully."
echo "============================================================"
