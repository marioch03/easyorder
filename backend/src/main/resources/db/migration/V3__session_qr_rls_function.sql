-- =====================================================================================
-- V3: FUNCIÓN SEGURA PARA RESOLUCIÓN DE SESIONES QR ANÓNIMAS BAJO RLS
-- =====================================================================================

-- Función con directiva SECURITY DEFINER que permite consultar la sesión activa
-- asociada a un código QR antes de conocer el id_tenant, evitando el bloqueo de RLS.
CREATE OR REPLACE FUNCTION get_active_session_by_qr(p_qr_code VARCHAR)
RETURNS TABLE (
    id BIGINT,
    tenant_id BIGINT,
    estado_nombre VARCHAR
)
SECURITY DEFINER
SET search_path = public
LANGUAGE plpgsql AS $$
BEGIN
    RETURN QUERY
    SELECT 
        s.id,
        s.id_tenant,
        e.nombre::VARCHAR AS estado_nombre
    FROM sesion s
    JOIN sesion_estado e ON s.id_estado = e.id
    WHERE s.qr_code_url = p_qr_code
      AND e.nombre = 'ACTIVA';
END;
$$;

-- Otorgar permiso de ejecución al usuario de aplicación
GRANT EXECUTE ON FUNCTION get_active_session_by_qr(VARCHAR) TO easyorder_app;
