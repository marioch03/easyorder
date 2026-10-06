-- =============================================================================
-- Migración V6: Soporte de Idempotencia en Pedidos (ORD-01)
-- Añade columna idempotency_key e índice único condicional por tenant.
-- =============================================================================

ALTER TABLE pedido ADD COLUMN IF NOT EXISTS idempotency_key VARCHAR(64);

-- Índice único compuesto por tenant para garantizar que dentro de un mismo restaurante
-- jamás se pueda insertar dos veces el mismo pedido con la misma clave de cliente.
-- El predicado WHERE idempotency_key IS NOT NULL permite múltiples filas con valor nulo
-- para mantener retrocompatibilidad total con peticiones sin cabecera.
CREATE UNIQUE INDEX IF NOT EXISTS idx_pedido_tenant_idempotency
ON pedido (id_tenant, idempotency_key)
WHERE idempotency_key IS NOT NULL;
