-- =====================================================================================
-- V5: BLINDAJE DE POLÍTICAS RLS CON CLÁUSULA EXPLÍCITA WITH CHECK
-- =====================================================================================

-- Actualizar las 12 políticas originales de V2 para garantizar que tanto la lectura (USING)
-- como la escritura/modificación (WITH CHECK) validen estrictamente el tenant actual.

ALTER POLICY isolation_policy_zona ON zona 
    USING (id_tenant = current_tenant_id()) 
    WITH CHECK (id_tenant = current_tenant_id());

ALTER POLICY isolation_policy_mesa ON mesa 
    USING (id_tenant = current_tenant_id()) 
    WITH CHECK (id_tenant = current_tenant_id());

ALTER POLICY isolation_policy_producto_tipo ON producto_tipo 
    USING (id_tenant = current_tenant_id()) 
    WITH CHECK (id_tenant = current_tenant_id());

ALTER POLICY isolation_policy_producto ON producto 
    USING (id_tenant = current_tenant_id()) 
    WITH CHECK (id_tenant = current_tenant_id());

ALTER POLICY isolation_policy_usuario ON usuario 
    USING (id_tenant = current_tenant_id()) 
    WITH CHECK (id_tenant = current_tenant_id());

ALTER POLICY isolation_policy_sesion ON sesion 
    USING (id_tenant = current_tenant_id()) 
    WITH CHECK (id_tenant = current_tenant_id());

ALTER POLICY isolation_policy_pedido ON pedido 
    USING (id_tenant = current_tenant_id()) 
    WITH CHECK (id_tenant = current_tenant_id());

ALTER POLICY isolation_policy_pedido_item ON pedido_item 
    USING (id_tenant = current_tenant_id()) 
    WITH CHECK (id_tenant = current_tenant_id());

ALTER POLICY isolation_policy_refresh_token ON refresh_token 
    USING (id_tenant = current_tenant_id()) 
    WITH CHECK (id_tenant = current_tenant_id());

ALTER POLICY isolation_policy_grupo_modificador ON grupo_modificador 
    USING (id_tenant = current_tenant_id()) 
    WITH CHECK (id_tenant = current_tenant_id());

ALTER POLICY isolation_policy_modificador ON modificador 
    USING (id_tenant = current_tenant_id()) 
    WITH CHECK (id_tenant = current_tenant_id());

ALTER POLICY isolation_policy_pedido_item_modificador ON pedido_item_modificador 
    USING (id_tenant = current_tenant_id()) 
    WITH CHECK (id_tenant = current_tenant_id());
