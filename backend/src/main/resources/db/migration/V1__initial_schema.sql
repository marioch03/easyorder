# ************************************************************
# Sequel Ace SQL dump
# Versión 20095
#
# https://sequel-ace.com/
# https://github.com/Sequel-Ace/Sequel-Ace
#
# Equipo: 127.0.0.1 (MySQL 11.8.2-MariaDB)
# Base de datos: AppTFG
# Tiempo de generación: 2026-07-09 09:14:11 +0000
# ************************************************************


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
SET NAMES utf8mb4;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE='NO_AUTO_VALUE_ON_ZERO', SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


# Volcado de tabla Alergeno
# ------------------------------------------------------------

DROP TABLE IF EXISTS `Alergeno`;

CREATE TABLE `Alergeno` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `nombre` (`nombre`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;



# Volcado de tabla Ingrediente
# ------------------------------------------------------------

DROP TABLE IF EXISTS `Ingrediente`;

CREATE TABLE `Ingrediente` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) NOT NULL,
  `stock` decimal(10,2) NOT NULL DEFAULT 0.00,
  `id_unidad` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `nombre` (`nombre`),
  KEY `fk_ingrediente_unidad` (`id_unidad`),
  CONSTRAINT `fk_ingrediente_unidad` FOREIGN KEY (`id_unidad`) REFERENCES `Unidad` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `check_stock` CHECK (`stock` >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;



# Volcado de tabla IngredienteAlergeno
# ------------------------------------------------------------

DROP TABLE IF EXISTS `IngredienteAlergeno`;

CREATE TABLE `IngredienteAlergeno` (
  `id_ingrediente` int(11) unsigned NOT NULL,
  `id_alergeno` int(11) unsigned NOT NULL,
  PRIMARY KEY (`id_ingrediente`,`id_alergeno`),
  KEY `alergeno_ingrediente` (`id_alergeno`),
  KEY `ingrediente_alergeno` (`id_ingrediente`),
  CONSTRAINT `alergeno_ingrediente` FOREIGN KEY (`id_alergeno`) REFERENCES `Alergeno` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `ingrediente_alergeno` FOREIGN KEY (`id_ingrediente`) REFERENCES `Ingrediente` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;



# Volcado de tabla Mesa
# ------------------------------------------------------------

DROP TABLE IF EXISTS `Mesa`;

CREATE TABLE `Mesa` (
  `numero` int(11) NOT NULL,
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `id_estado` bigint(20) NOT NULL,
  `id_zona` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK3k3y7rcmuy3pew6rgq6o0q7k7` (`numero`),
  KEY `FK4q943oss3nebh2ya785ih2amx` (`id_estado`),
  KEY `FKp9jix7w7d0o0g3xrb087lss5e` (`id_zona`),
  CONSTRAINT `FK4q943oss3nebh2ya785ih2amx` FOREIGN KEY (`id_estado`) REFERENCES `MesaEstado` (`id`),
  CONSTRAINT `FKp9jix7w7d0o0g3xrb087lss5e` FOREIGN KEY (`id_zona`) REFERENCES `Zona` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

LOCK TABLES `Mesa` WRITE;
/*!40000 ALTER TABLE `Mesa` DISABLE KEYS */;

INSERT INTO `Mesa` (`numero`, `id`, `id_estado`, `id_zona`)
VALUES
	(1,1,1,1),
	(2,2,1,1),
	(3,3,1,1),
	(4,4,1,1),
	(5,6,1,1),
	(6,7,1,2),
	(7,8,1,2),
	(8,9,1,2),
	(9,10,1,2),
	(10,12,1,2);

/*!40000 ALTER TABLE `Mesa` ENABLE KEYS */;
UNLOCK TABLES;


# Volcado de tabla MesaEstado
# ------------------------------------------------------------

DROP TABLE IF EXISTS `MesaEstado`;

CREATE TABLE `MesaEstado` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKh2xyrihsimbsh4whks5wa3ndv` (`nombre`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

LOCK TABLES `MesaEstado` WRITE;
/*!40000 ALTER TABLE `MesaEstado` DISABLE KEYS */;

INSERT INTO `MesaEstado` (`id`, `nombre`)
VALUES
	(4,'ESPERANDO_CUENTA'),
	(3,'ESPERANDO_PEDIDO'),
	(1,'LIBRE'),
	(2,'OCUPADA');

/*!40000 ALTER TABLE `MesaEstado` ENABLE KEYS */;
UNLOCK TABLES;


# Volcado de tabla Pedido
# ------------------------------------------------------------

DROP TABLE IF EXISTS `Pedido`;

CREATE TABLE `Pedido` (
  `created_at` datetime(6) DEFAULT NULL,
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `id_estado` bigint(20) NOT NULL,
  `id_sesion` bigint(20) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK4ry16ug2ow3g9606foyfpnjkj` (`id_estado`),
  KEY `FK482vn8vujf0mxlk6cy35nh2bp` (`id_sesion`),
  CONSTRAINT `FK482vn8vujf0mxlk6cy35nh2bp` FOREIGN KEY (`id_sesion`) REFERENCES `Sesion` (`id`),
  CONSTRAINT `FK4ry16ug2ow3g9606foyfpnjkj` FOREIGN KEY (`id_estado`) REFERENCES `PedidoEstado` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=70 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

# Volcado de tabla PedidoEstado
# ------------------------------------------------------------

DROP TABLE IF EXISTS `PedidoEstado`;

CREATE TABLE `PedidoEstado` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(50) NOT NULL,
  `descripcion` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKp5hog9utiqevh05vn5m5y1eej` (`nombre`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

LOCK TABLES `PedidoEstado` WRITE;
/*!40000 ALTER TABLE `PedidoEstado` DISABLE KEYS */;

INSERT INTO `PedidoEstado` (`id`, `nombre`, `descripcion`)
VALUES
	(1,'PENDIENTE',NULL),
	(2,'PARCIAL',NULL),
	(3,'LISTO',NULL),
	(4,'SERVIDO',NULL),
	(5,'CANCELADO',NULL);

/*!40000 ALTER TABLE `PedidoEstado` ENABLE KEYS */;
UNLOCK TABLES;


# Volcado de tabla PedidoItem
# ------------------------------------------------------------

DROP TABLE IF EXISTS `PedidoItem`;

CREATE TABLE `PedidoItem` (
  `cantidad` int(11) NOT NULL,
  `listo_para_servir` bit(1) DEFAULT NULL,
  `precio_unitario` decimal(10,2) NOT NULL,
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `id_pedido` bigint(20) NOT NULL,
  `id_producto` bigint(20) NOT NULL,
  `nota` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK5pey5gb8wnracubsm1jyi2n3q` (`id_pedido`),
  KEY `FK4lac2n1fp0sn1u49831p7y9r1` (`id_producto`),
  CONSTRAINT `FK4lac2n1fp0sn1u49831p7y9r1` FOREIGN KEY (`id_producto`) REFERENCES `Producto` (`id`),
  CONSTRAINT `FK5pey5gb8wnracubsm1jyi2n3q` FOREIGN KEY (`id_pedido`) REFERENCES `Pedido` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=151 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

# Volcado de tabla Producto
# ------------------------------------------------------------

DROP TABLE IF EXISTS `Producto`;

CREATE TABLE `Producto` (
  `disponible` bit(1) NOT NULL,
  `precio` decimal(10,2) NOT NULL,
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `id_tipo` bigint(20) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `descripcion` varchar(200) DEFAULT NULL,
  `imagen` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKyll5n3w9acrph6fel7dqbd1b` (`nombre`),
  KEY `FK3i0a4bsm6ttske8p4b9s58ohk` (`id_tipo`),
  CONSTRAINT `FK3i0a4bsm6ttske8p4b9s58ohk` FOREIGN KEY (`id_tipo`) REFERENCES `ProductoTipo` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

LOCK TABLES `Producto` WRITE;
/*!40000 ALTER TABLE `Producto` DISABLE KEYS */;

INSERT INTO `Producto` (`disponible`, `precio`, `id`, `id_tipo`, `nombre`, `descripcion`, `imagen`)
VALUES
	(b'1',1.50,1,4,'Agua grande','Botella de agua 1L','agua1.png'),
	(b'1',1.00,2,4,'Agua pequeña','Botella de agua 500ml','agua2.png'),
	(b'1',1.80,3,4,'Refresco','Bebida refrescante con gas','cola.jpg'),
	(b'1',2.50,4,4,'Cerveza','Cerveza fría de barril o botella','cerveza.png'),
	(b'1',3.00,5,4,'Vino','Copa de vino tinto','vino.png'),
	(b'1',2.50,6,4,'Tinto de verano','Vino con gaseosa y limón','tinto.png'),
	(b'1',1.80,8,4,'Zumo','Zumo natural de frutas','zumo.png'),
	(b'1',2.50,9,1,'Tortilla de patatas','Tapa tradicional española de patata y huevo','tortilla.jpg'),
	(b'1',2.80,10,1,'Croquetas caseras','Croquetas cremosas de jamón','croquetas.jpg'),
	(b'1',2.50,11,1,'Patatas bravas','Patatas fritas con salsa brava','bravas.png'),
	(b'1',2.50,12,1,'Ensaladilla rusa','Patata, atún, huevo y mayonesa','ensaladilla.png'),
	(b'1',3.20,13,1,'Calamares fritos','Anillas de calamar rebozadas','calamares.png'),
	(b'1',4.50,14,2,'Gambas al ajillo','Gambas salteadas con ajo y guindilla','gambas.jpg'),
	(b'1',5.50,15,2,'Jamón ibérico','Ración de jamón ibérico cortado a mano','jamon.jpg'),
	(b'1',4.00,16,2,'Queso curado','Tabla de queso curado de oveja','queso.jpg'),
	(b'1',4.50,17,2,'Huevos rotos','Patatas con huevos fritos y jamón','huevos.jpg'),
	(b'1',4.50,18,2,'Albóndigas','Albóndigas caseras en salsa de tomate','albondigas.jpg'),
	(b'1',2.50,19,3,'Flan de queso','Flan casero cremoso de queso','flan.png'),
	(b'1',1.80,20,3,'Yogur','Yogur natural o de sabores','yogur.jpg'),
	(b'1',2.00,21,3,'Natillas','Natillas caseras con canela','natillas.png'),
	(b'1',2.50,22,3,'Helado','Copa de helado variado','helado.jpg');

/*!40000 ALTER TABLE `Producto` ENABLE KEYS */;
UNLOCK TABLES;


# Volcado de tabla ProductoTipo
# ------------------------------------------------------------

DROP TABLE IF EXISTS `ProductoTipo`;

CREATE TABLE `ProductoTipo` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) NOT NULL,
  `descripcion` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKq6ghvuxhnh183bm7jsm8t1svr` (`nombre`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

LOCK TABLES `ProductoTipo` WRITE;
/*!40000 ALTER TABLE `ProductoTipo` DISABLE KEYS */;

INSERT INTO `ProductoTipo` (`id`, `nombre`, `descripcion`)
VALUES
	(1,'TAPA',NULL),
	(2,'RACION',NULL),
	(3,'POSTRE',NULL),
	(4,'BEBIDA',NULL);

/*!40000 ALTER TABLE `ProductoTipo` ENABLE KEYS */;
UNLOCK TABLES;


# Volcado de tabla Sesion
# ------------------------------------------------------------

DROP TABLE IF EXISTS `Sesion`;

CREATE TABLE `Sesion` (
  `hora_fin` datetime(6) DEFAULT NULL,
  `hora_inicio` datetime DEFAULT current_timestamp(),
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `id_estado` bigint(20) NOT NULL,
  `id_mesa` bigint(20) NOT NULL,
  `id_usuario` bigint(20) DEFAULT NULL,
  `qr_code_url` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK3bj8mkfjbloc6hmagn7qtmo6x` (`id_estado`),
  KEY `FKm3pyowo53mnfwl60k7o664huu` (`id_mesa`),
  KEY `FK9khsc6p8s2paely27hncpc42y` (`id_usuario`),
  CONSTRAINT `FK3bj8mkfjbloc6hmagn7qtmo6x` FOREIGN KEY (`id_estado`) REFERENCES `SesionEstado` (`id`),
  CONSTRAINT `FK9khsc6p8s2paely27hncpc42y` FOREIGN KEY (`id_usuario`) REFERENCES `Usuario` (`id`),
  CONSTRAINT `FKm3pyowo53mnfwl60k7o664huu` FOREIGN KEY (`id_mesa`) REFERENCES `Mesa` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=124 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

# Volcado de tabla SesionEstado
# ------------------------------------------------------------

DROP TABLE IF EXISTS `SesionEstado`;

CREATE TABLE `SesionEstado` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) NOT NULL,
  `descripcion` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKo8udgkr90isij9un3f49qwhs` (`nombre`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

LOCK TABLES `SesionEstado` WRITE;
/*!40000 ALTER TABLE `SesionEstado` DISABLE KEYS */;

INSERT INTO `SesionEstado` (`id`, `nombre`, `descripcion`)
VALUES
	(1,'ACTIVA',NULL),
	(2,'FINALIZADA',NULL),
	(3,'CANCELADA',NULL);

/*!40000 ALTER TABLE `SesionEstado` ENABLE KEYS */;
UNLOCK TABLES;


# Volcado de tabla Token
# ------------------------------------------------------------

DROP TABLE IF EXISTS `Token`;

CREATE TABLE `Token` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `expired` bit(1) NOT NULL,
  `revoked` bit(1) NOT NULL,
  `token` varchar(255) DEFAULT NULL,
  `tokenType` enum('BEARER') DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK3wi2t4g8oiplxjflw3o2lkv2y` (`token`),
  KEY `FKk13h7kei78pxj7yv2w3kky79v` (`user_id`),
  CONSTRAINT `FKk13h7kei78pxj7yv2w3kky79v` FOREIGN KEY (`user_id`) REFERENCES `Usuario` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=259 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


# Volcado de tabla Unidad
# ------------------------------------------------------------

DROP TABLE IF EXISTS `Unidad`;

CREATE TABLE `Unidad` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `nombre` varchar(50) NOT NULL,
  `simbolo` varchar(10) NOT NULL,
  `factor` int(10) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `nombre` (`nombre`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

LOCK TABLES `Unidad` WRITE;
/*!40000 ALTER TABLE `Unidad` DISABLE KEYS */;

INSERT INTO `Unidad` (`id`, `nombre`, `simbolo`, `factor`)
VALUES
	(1,'gramo','g',1),
	(2,'kilogramo','kg',1000),
	(3,'litro','L',1000),
	(4,'mililitro','ml',1),
	(5,'unidad','u',1);

/*!40000 ALTER TABLE `Unidad` ENABLE KEYS */;
UNLOCK TABLES;


# Volcado de tabla Usuario
# ------------------------------------------------------------

DROP TABLE IF EXISTS `Usuario`;

CREATE TABLE `Usuario` (
  `activo` bit(1) NOT NULL,
  `created_at` datetime DEFAULT current_timestamp(),
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `rol` bigint(20) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `clave` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKkg8o06fnholoqdqved4kgtmyw` (`rol`),
  CONSTRAINT `FKkg8o06fnholoqdqved4kgtmyw` FOREIGN KEY (`rol`) REFERENCES `UsuarioRol` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

LOCK TABLES `Usuario` WRITE;
/*!40000 ALTER TABLE `Usuario` DISABLE KEYS */;

INSERT INTO `Usuario` (`activo`, `created_at`, `id`, `rol`, `nombre`, `clave`)
VALUES
	(b'1','2026-04-24 12:32:49',1,1,'Mario','$2a$10$92MiowW9DXGXAIR9N7IyKe/2y8ZQgHchsUi.UKV8Tsfd7loIpQfMG'),
	(b'1','2026-06-26 20:52:42',2,2,'Pepe','$2a$10$As0Y2nrwk.vQ82.CJSghMeigJJXNut.bjUmU2KqSDGKBR68122kuC');

/*!40000 ALTER TABLE `Usuario` ENABLE KEYS */;
UNLOCK TABLES;


# Volcado de tabla UsuarioRol
# ------------------------------------------------------------

DROP TABLE IF EXISTS `UsuarioRol`;

CREATE TABLE `UsuarioRol` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(50) NOT NULL,
  `descripcion` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKkosmoqjf29n5u87eu710d4xjv` (`nombre`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

LOCK TABLES `UsuarioRol` WRITE;
/*!40000 ALTER TABLE `UsuarioRol` DISABLE KEYS */;

INSERT INTO `UsuarioRol` (`id`, `nombre`, `descripcion`)
VALUES
	(1,'ADMIN',NULL),
	(2,'PERSONAL',NULL);

/*!40000 ALTER TABLE `UsuarioRol` ENABLE KEYS */;
UNLOCK TABLES;


# Volcado de tabla Zona
# ------------------------------------------------------------

DROP TABLE IF EXISTS `Zona`;

CREATE TABLE `Zona` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(50) NOT NULL,
  `descripcion` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKi5l7hfymoeq29irj15xq8nnv6` (`nombre`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

LOCK TABLES `Zona` WRITE;
/*!40000 ALTER TABLE `Zona` DISABLE KEYS */;

INSERT INTO `Zona` (`id`, `nombre`, `descripcion`)
VALUES
	(1,'INTERIOR',NULL),
	(2,'EXTERIOR',NULL);

/*!40000 ALTER TABLE `Zona` ENABLE KEYS */;
UNLOCK TABLES;



/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
