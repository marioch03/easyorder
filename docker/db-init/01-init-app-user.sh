#!/bin/sh
set -e

# Este script se ejecuta automáticamente SOLO la primera vez que se inicializa el volumen de PostgreSQL.
# Crea el usuario de aplicación sin permisos de superusuario y con NOBYPASSRLS forzado.

echo "=== Configurando usuario de aplicación no-superusuario para RLS ==="

APP_USER="${APP_DB_USER:-easyorder_app}"
APP_PASS="${APP_DB_PASSWORD:-easyorder_secret}"

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
    DO \$\$
    BEGIN
      IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = '${APP_USER}') THEN
        CREATE ROLE ${APP_USER} LOGIN PASSWORD '${APP_PASS}' NOSUPERUSER NOBYPASSRLS NOCREATEDB NOCREATEROLE;
      END IF;
    END
    \$\$;

    -- Permisos de conexión y esquema
    GRANT CONNECT ON DATABASE ${POSTGRES_DB} TO ${APP_USER};
    GRANT USAGE ON SCHEMA public TO ${APP_USER};

    -- Permisos sobre tablas y secuencias existentes
    GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO ${APP_USER};
    GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO ${APP_USER};

    -- Permisos por defecto para cualquier tabla, secuencia o función que cree Flyway (como postgres) en el futuro
    ALTER DEFAULT PRIVILEGES FOR ROLE ${POSTGRES_USER} IN SCHEMA public
      GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO ${APP_USER};

    ALTER DEFAULT PRIVILEGES FOR ROLE ${POSTGRES_USER} IN SCHEMA public
      GRANT USAGE, SELECT ON SEQUENCES TO ${APP_USER};

    ALTER DEFAULT PRIVILEGES FOR ROLE ${POSTGRES_USER} IN SCHEMA public
      GRANT EXECUTE ON FUNCTIONS TO ${APP_USER};
EOSQL

echo "=== Usuario '${APP_USER}' configurado exitosamente con NOBYPASSRLS ==="
