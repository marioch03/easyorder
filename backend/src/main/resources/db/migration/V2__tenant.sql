-- =====================================================================================
-- 4. CONFIGURACIÓN DE SEGURIDAD A NIVEL DE FILA (RLS)
-- =====================================================================================

-- 4.1 Forzar RLS (incluso para los dueños de las tablas si no son superusuarios)
ALTER TABLE zona ENABLE ROW LEVEL SECURITY;
ALTER TABLE zona FORCE ROW LEVEL SECURITY;

ALTER TABLE mesa ENABLE ROW LEVEL SECURITY;
ALTER TABLE mesa FORCE ROW LEVEL SECURITY;

ALTER TABLE producto_tipo ENABLE ROW LEVEL SECURITY;
ALTER TABLE producto_tipo FORCE ROW LEVEL SECURITY;

ALTER TABLE producto ENABLE ROW LEVEL SECURITY;
ALTER TABLE producto FORCE ROW LEVEL SECURITY;

ALTER TABLE usuario ENABLE ROW LEVEL SECURITY;
ALTER TABLE usuario FORCE ROW LEVEL SECURITY;

ALTER TABLE sesion ENABLE ROW LEVEL SECURITY;
ALTER TABLE sesion FORCE ROW LEVEL SECURITY;

ALTER TABLE pedido ENABLE ROW LEVEL SECURITY;
ALTER TABLE pedido FORCE ROW LEVEL SECURITY;

ALTER TABLE pedido_item ENABLE ROW LEVEL SECURITY;
ALTER TABLE pedido_item FORCE ROW LEVEL SECURITY;

ALTER TABLE token ENABLE ROW LEVEL SECURITY;
ALTER TABLE token FORCE ROW LEVEL SECURITY;

ALTER TABLE grupo_modificador ENABLE ROW LEVEL SECURITY;
ALTER TABLE grupo_modificador FORCE ROW LEVEL SECURITY;

ALTER TABLE modificador ENABLE ROW LEVEL SECURITY;
ALTER TABLE modificador FORCE ROW LEVEL SECURITY;

ALTER TABLE pedido_item_modificador ENABLE ROW LEVEL SECURITY;
ALTER TABLE pedido_item_modificador FORCE ROW LEVEL SECURITY;

-- 4.2 Crear una función para obtener el tenant actual de forma segura
-- Si la variable no está seteada (ej. un script interno), devuelve NULL y bloquea el acceso.
CREATE OR REPLACE FUNCTION current_tenant_id() RETURNS BIGINT
LANGUAGE sql STABLE AS $$
    SELECT NULLIF(current_setting('app.current_tenant_id', true), '')::BIGINT;
$$;

-- 4.3 Crear las políticas de aislamiento
-- Usamos "ALL" para cubrir SELECT, INSERT, UPDATE y DELETE.

CREATE POLICY isolation_policy_zona ON zona 
    FOR ALL USING (id_tenant = current_tenant_id());

CREATE POLICY isolation_policy_mesa ON mesa 
    FOR ALL USING (id_tenant = current_tenant_id());

CREATE POLICY isolation_policy_producto_tipo ON producto_tipo 
    FOR ALL USING (id_tenant = current_tenant_id());

CREATE POLICY isolation_policy_producto ON producto 
    FOR ALL USING (id_tenant = current_tenant_id());

CREATE POLICY isolation_policy_usuario ON usuario 
    FOR ALL USING (id_tenant = current_tenant_id());

CREATE POLICY isolation_policy_sesion ON sesion 
    FOR ALL USING (id_tenant = current_tenant_id());

CREATE POLICY isolation_policy_pedido ON pedido 
    FOR ALL USING (id_tenant = current_tenant_id());

CREATE POLICY isolation_policy_pedido_item ON pedido_item 
    FOR ALL USING (id_tenant = current_tenant_id());

CREATE POLICY isolation_policy_token ON token 
    FOR ALL USING (id_tenant = current_tenant_id());

CREATE POLICY isolation_policy_grupo_modificador ON grupo_modificador 
    FOR ALL USING (id_tenant = current_tenant_id());

CREATE POLICY isolation_policy_modificador ON modificador 
    FOR ALL USING (id_tenant = current_tenant_id());

CREATE POLICY isolation_policy_pedido_item_modificador ON pedido_item_modificador 
    FOR ALL USING (id_tenant = current_tenant_id());

-- EXCEPCIÓN: La tabla Tenant NO lleva RLS por id_tenant en sí misma, 
-- ya que el superadmin o el registro inicial necesita leerla completa.
