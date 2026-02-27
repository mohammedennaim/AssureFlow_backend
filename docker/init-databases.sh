#!/bin/bash
# ============================================================
# 🗄️ Script d'initialisation PostgreSQL
# ============================================================
# Ce script crée automatiquement une base de données par service
# Il est exécuté une seule fois au premier démarrage du conteneur PostgreSQL
# ============================================================

set -e

echo "🚀 Initialisation des bases de données pour l'Insurance Platform..."

# Fonction pour créer une base de données si elle n'existe pas
create_database() {
    local database=$1
    echo "📦 Création de la base de données : $database"
    psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
        SELECT 'CREATE DATABASE $database'
        WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = '$database')\gexec
        GRANT ALL PRIVILEGES ON DATABASE $database TO $POSTGRES_USER;
EOSQL
    echo "✅ Base de données '$database' créée avec succès."
}

# Créer une DB par microservice (Database-per-Service pattern)
create_database "policy_db"
create_database "client_db"
create_database "billing_db"
create_database "claims_db"
create_database "notification_db"
create_database "iam_db"
create_database "workflow_db"

echo "🎉 Toutes les bases de données ont été initialisées !"
