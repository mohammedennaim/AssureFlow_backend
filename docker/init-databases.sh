#!/bin/bash
set -e

echo "Initialisation des bases de donnees pour Insurance Platform..."

create_database() {
    local database=$1
    echo "Creation de la base de donnees : $database"
    psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
        SELECT 'CREATE DATABASE $database'
        WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = '$database')\gexec
        GRANT ALL PRIVILEGES ON DATABASE $database TO $POSTGRES_USER;
EOSQL
    echo "Base de donnees '$database' creee avec succes."
}

create_database "policy_db"
create_database "client_db"
create_database "billing_db"
create_database "claims_db"
create_database "notification_db"
create_database "iam_db"
create_database "workflow_db"

echo "Toutes les bases de donnees ont ete initialisees !"
