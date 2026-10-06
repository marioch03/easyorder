-- =============================================================================
-- Migración V8: Soporte de Soft-Delete en Mesas (ORD-04)
-- Añade columna activo y sustituye restricción UNIQUE por índice parcial.
-- =============================================================================

-- 1. Añadir columna activo con valor por defecto TRUE
ALTER TABLE mesa ADD COLUMN IF NOT EXISTS activo BOOLEAN NOT NULL DEFAULT TRUE;

-- 2. Eliminar la restricción UNIQUE previa que impedía reutilizar números de mesas eliminadas
ALTER TABLE mesa DROP CONSTRAINT IF EXISTS uk_mesa_tenant_numero;

-- 3. Crear índice UNIQUE condicional/parcial solo sobre mesas activas
-- Permite que coexistan mesas inactivas históricas con el mismo número sin conflicto.
CREATE UNIQUE INDEX IF NOT EXISTS uk_mesa_tenant_numero_activo
ON mesa (id_tenant, numero)
WHERE activo = TRUE;
