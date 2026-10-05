-- =====================================================================================
-- V4: AISLAMIENTO MULTI-TENANT Y RLS PARA TABLAS RELACIONALES INTERMEDIAS
-- =====================================================================================

-- 1. TABLA producto_alergeno
-- Añadir columna id_tenant
ALTER TABLE producto_alergeno ADD COLUMN id_tenant BIGINT;

-- Poblar id_tenant desde la tabla padre producto
UPDATE producto_alergeno pa
SET id_tenant = p.id_tenant
FROM producto p
WHERE pa.id_producto = p.id;

-- Establecer NOT NULL y clave foránea
ALTER TABLE producto_alergeno ALTER COLUMN id_tenant SET NOT NULL;
ALTER TABLE producto_alergeno 
    ADD CONSTRAINT fk_producto_alergeno_tenant FOREIGN KEY (id_tenant) REFERENCES tenant(id) ON DELETE CASCADE;

-- Índices para optimización de consultas filtradas por tenant
CREATE INDEX idx_producto_alergeno_tenant ON producto_alergeno (id_tenant);
CREATE INDEX idx_producto_alergeno_tenant_producto ON producto_alergeno (id_tenant, id_producto);

-- Habilitar y forzar Row Level Security (RLS)
ALTER TABLE producto_alergeno ENABLE ROW LEVEL SECURITY;
ALTER TABLE producto_alergeno FORCE ROW LEVEL SECURITY;

-- Política de aislamiento con USING y WITH CHECK
CREATE POLICY isolation_policy_producto_alergeno ON producto_alergeno
    FOR ALL 
    USING (id_tenant = current_tenant_id())
    WITH CHECK (id_tenant = current_tenant_id());


-- 2. TABLA producto_grupo_modificador
-- Añadir columna id_tenant
ALTER TABLE producto_grupo_modificador ADD COLUMN id_tenant BIGINT;

-- Poblar id_tenant desde la tabla padre producto
UPDATE producto_grupo_modificador pgm
SET id_tenant = p.id_tenant
FROM producto p
WHERE pgm.id_producto = p.id;

-- Establecer NOT NULL y clave foránea
ALTER TABLE producto_grupo_modificador ALTER COLUMN id_tenant SET NOT NULL;
ALTER TABLE producto_grupo_modificador 
    ADD CONSTRAINT fk_pgm_tenant FOREIGN KEY (id_tenant) REFERENCES tenant(id) ON DELETE CASCADE;

-- Índices para optimización de consultas filtradas por tenant
CREATE INDEX idx_pgm_tenant ON producto_grupo_modificador (id_tenant);
CREATE INDEX idx_pgm_tenant_producto ON producto_grupo_modificador (id_tenant, id_producto);

-- Habilitar y forzar Row Level Security (RLS)
ALTER TABLE producto_grupo_modificador ENABLE ROW LEVEL SECURITY;
ALTER TABLE producto_grupo_modificador FORCE ROW LEVEL SECURITY;

-- Política de aislamiento con USING y WITH CHECK
CREATE POLICY isolation_policy_producto_grupo_modificador ON producto_grupo_modificador
    FOR ALL 
    USING (id_tenant = current_tenant_id())
    WITH CHECK (id_tenant = current_tenant_id());


-- 3. PERMISOS DML PARA EL USUARIO DE APLICACIÓN
GRANT SELECT, INSERT, UPDATE, DELETE ON producto_alergeno TO easyorder_app;
GRANT SELECT, INSERT, UPDATE, DELETE ON producto_grupo_modificador TO easyorder_app;
