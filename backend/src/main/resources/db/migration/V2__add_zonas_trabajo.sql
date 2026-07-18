-- Migración V2: Añadir Zonas de Trabajo, Roles y actualizar PedidoItem

-- 1. Añadir nuevos roles: COCINA y BARRA
INSERT INTO `UsuarioRol` (`nombre`, `descripcion`) VALUES 
('COCINA', 'Rol para usuarios de estaciones en cocina (KDS)'),
('BARRA', 'Rol para usuarios de estaciones en barra');

-- 2. Crear tabla ZonaTrabajo
CREATE TABLE `ZonaTrabajo` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_zona_trabajo_nombre` (`nombre`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- 3. Insertar Zonas de Trabajo base
INSERT INTO `ZonaTrabajo` (`nombre`) VALUES 
('COCINA'), 
('BARRA');

-- 4. Modificar PedidoItem: añadir id_zona_trabajo y flag servido
ALTER TABLE `PedidoItem`
ADD COLUMN `id_zona_trabajo` bigint(20) DEFAULT NULL,
ADD COLUMN `servido` bit(1) DEFAULT b'0',
ADD CONSTRAINT `FK_pedido_item_zona_trabajo` FOREIGN KEY (`id_zona_trabajo`) REFERENCES `ZonaTrabajo` (`id`);