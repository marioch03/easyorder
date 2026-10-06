-- =============================================================================
-- Migración V7: Soporte de Bloqueo Optimista (@Version - ORD-02)
-- Añade la columna 'version' a las entidades transaccionales concurrentes.
-- =============================================================================

ALTER TABLE mesa ADD COLUMN IF NOT EXISTS version BIGINT NOT NULL DEFAULT 0;
ALTER TABLE sesion ADD COLUMN IF NOT EXISTS version BIGINT NOT NULL DEFAULT 0;
ALTER TABLE pedido ADD COLUMN IF NOT EXISTS version BIGINT NOT NULL DEFAULT 0;
ALTER TABLE pedido_item ADD COLUMN IF NOT EXISTS version BIGINT NOT NULL DEFAULT 0;
