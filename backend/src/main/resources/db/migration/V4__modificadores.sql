-- 1. GRUPOS DE MODIFICADORES (Ej: "Punto de la carne", "Extras", "Tipo de leche")
DROP TABLE IF EXISTS GrupoModificador;
CREATE TABLE GrupoModificador (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  nombre varchar(100) NOT NULL,
  seleccion_minima int(11) NOT NULL DEFAULT 0, -- 1 = Obligatorio (ej: Punto carne), 0 = Opcional
  seleccion_maxima int(11) NOT NULL DEFAULT 1, -- Cuántas opciones puede marcar (ej: max 3 extras)
  activo bit(1) NOT NULL DEFAULT b'1',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- 2. MODIFICADORES U OPCIONES (Ej: "Poco hecho", "Queso de cabra", "Leche de soja")
DROP TABLE IF EXISTS Modificador;
CREATE TABLE Modificador (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  id_grupo bigint(20) NOT NULL,
  nombre varchar(100) NOT NULL,
  precio_extra decimal(10,2) NOT NULL DEFAULT 0.00, -- 0.00 si no cobra suplemento
  activo bit(1) NOT NULL DEFAULT b'1',
  PRIMARY KEY (id),
  KEY FK_Modificador_Grupo (id_grupo),
  CONSTRAINT FK_Modificador_Grupo FOREIGN KEY (id_grupo) REFERENCES GrupoModificador (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- 3. RELACIÓN PRODUCTO - GRUPOS (¿Qué modificadores tiene qué producto?)
DROP TABLE IF EXISTS Producto_GrupoModificador;
CREATE TABLE Producto_GrupoModificador (
  id_producto bigint(20) NOT NULL,
  id_grupo bigint(20) NOT NULL,
  orden_visual int(11) DEFAULT 0, -- Para ordenarlos en tu App/Web (ej: 1º Carne, 2º Extras)
  PRIMARY KEY (id_producto, id_grupo),
  KEY FK_PGM_Producto (id_producto),
  KEY FK_PGM_Grupo (id_grupo),
  CONSTRAINT FK_PGM_Producto FOREIGN KEY (id_producto) REFERENCES Producto (id),
  CONSTRAINT FK_PGM_Grupo FOREIGN KEY (id_grupo) REFERENCES GrupoModificador (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- 4. MODIFICADORES SELECCIONADOS EN LA LÍNEA DE PEDIDO
DROP TABLE IF EXISTS PedidoItem_Modificador;
CREATE TABLE PedidoItem_Modificador (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  id_pedido_item bigint(20) NOT NULL,
  id_modificador bigint(20) NOT NULL,
  cantidad int(11) NOT NULL DEFAULT 1, -- Por si piden "Doble de queso" en el mismo modificador
  precio_aplicado decimal(10,2) NOT NULL, -- Copia del 'precio_extra' en el momento de la compra
  PRIMARY KEY (id),
  KEY FK_PIM_PedidoItem (id_pedido_item),
  KEY FK_PIM_Modificador (id_modificador),
  CONSTRAINT FK_PIM_PedidoItem FOREIGN KEY (id_pedido_item) REFERENCES PedidoItem (id) ON DELETE CASCADE,
  CONSTRAINT FK_PIM_Modificador FOREIGN KEY (id_modificador) REFERENCES Modificador (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

ALTER TABLE Pedido ADD COLUMN total decimal(10,2) NOT NULL DEFAULT 0.00;
-- 1. Insertamos el producto (ID 999 para la prueba). 
-- Ojo: id_tipo=2 corresponde a 'RACION' según tu tabla ProductoTipo
-- Añadimos id_tenant (suponiendo que el tenant 1 ya existe en la tabla Tenant)
INSERT INTO Producto (id, id_tenant, disponible, precio, id_tipo, nombre, descripcion, imagen) 
VALUES (999, 1, b'1', 4.50, 2, 'Hamburguesa', 'Hamburguesa casera con queso y patatas', 'food.png');

-- ==========================================
-- GRUPO 1: PUNTO DE CARNE (Obligatorio)
-- ==========================================
INSERT INTO GrupoModificador (id, nombre, seleccion_minima, seleccion_maxima, activo) 
VALUES (101, 'Punto de carne', 1, 1, b'1');

INSERT INTO Modificador (id, id_grupo, nombre, precio_extra, activo) VALUES 
(1011, 101, 'Poco hecha', 0.00, b'1'),
(1012, 101, 'Al punto', 0.00, b'1'),
(1013, 101, 'Muy hecha', 0.00, b'1');

-- ==========================================
-- GRUPO 2: EXTRAS (Opcional y múltiple)
-- ==========================================
INSERT INTO GrupoModificador (id, nombre, seleccion_minima, seleccion_maxima, activo) 
VALUES (102, 'Extras', 0, 2, b'1');

INSERT INTO Modificador (id, id_grupo, nombre, precio_extra, activo) VALUES 
(1021, 102, 'Extra Bacon', 1.00, b'1'),
(1022, 102, 'Extra Queso', 1.00, b'1');

-- ==========================================
-- VINCULACIÓN: PRODUCTO <-> GRUPOS
-- ==========================================
INSERT INTO Producto_GrupoModificador (id_producto, id_grupo, orden_visual) VALUES 
(999, 101, 1),
(999, 102, 2);