DROP TABLE IF EXISTS `Tenant`;

CREATE TABLE Tenant (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(120) NOT NULL,
    slug VARCHAR(60) NOT NULL,          -- útil si algún día identificas el tenant por subdominio (barpepe.tuapp.com)
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_tenant_slug UNIQUE (slug)
);

-- Migración segura de tu bar actual, que hoy no tiene ningún concepto de
-- tenant: se crea como Tenant #1 y todo lo existente se le asigna.
INSERT INTO Tenant (id, nombre, slug) VALUES (1, 'Casa Cele', 'casacele');
INSERT INTO Tenant (id, nombre, slug) VALUES (2, 'Futbolista', 'futbolista');


-- Patrón para cada tabla afectada (repite para Zona, Mesa, Producto,
-- ProductoTipo, Usuario, Sesion, Pedido, Ingrediente si aplica):
ALTER TABLE Mesa ADD COLUMN id_tenant BIGINT NULL;
UPDATE Mesa SET id_tenant = 1;
ALTER TABLE Mesa MODIFY COLUMN id_tenant BIGINT NOT NULL;
ALTER TABLE Mesa ADD CONSTRAINT fk_mesa_tenant FOREIGN KEY (id_tenant) REFERENCES Tenant(id);
ALTER TABLE Mesa DROP INDEX UK3k3y7rcmuy3pew6rgq6o0q7k7;
ALTER TABLE Mesa ADD CONSTRAINT uk_mesa_tenant_numero UNIQUE (id_tenant, numero);

ALTER TABLE Zona ADD COLUMN id_tenant BIGINT NULL;
UPDATE Zona SET id_tenant = 1;
ALTER TABLE Zona MODIFY COLUMN id_tenant BIGINT NOT NULL;
ALTER TABLE Zona ADD CONSTRAINT fk_zona_tenant FOREIGN KEY (id_tenant) REFERENCES Tenant(id);
ALTER TABLE Zona DROP INDEX UKi5l7hfymoeq29irj15xq8nnv6;

ALTER TABLE Zona ADD CONSTRAINT uk_zona_tenant_nombre UNIQUE (id_tenant, nombre);

ALTER TABLE Pedido ADD COLUMN id_tenant BIGINT NULL;
UPDATE Pedido SET id_tenant = 1;
ALTER TABLE Pedido MODIFY COLUMN id_tenant BIGINT NOT NULL;
ALTER TABLE Pedido ADD CONSTRAINT fk_pedido_tenant FOREIGN KEY (id_tenant) REFERENCES Tenant(id);

ALTER TABLE Producto ADD COLUMN id_tenant BIGINT NULL;
UPDATE Producto SET id_tenant = 1;
ALTER TABLE Producto MODIFY COLUMN id_tenant BIGINT NOT NULL;
ALTER TABLE Producto ADD CONSTRAINT fk_producto_tenant FOREIGN KEY (id_tenant) REFERENCES Tenant(id);
ALTER TABLE Producto DROP INDEX UKyll5n3w9acrph6fel7dqbd1b;
ALTER TABLE Producto ADD CONSTRAINT uk_producto_tenant_nombre UNIQUE (id_tenant, nombre);

ALTER TABLE ProductoTipo ADD COLUMN id_tenant BIGINT NULL;
UPDATE ProductoTipo SET id_tenant = 1;
ALTER TABLE ProductoTipo MODIFY COLUMN id_tenant BIGINT NOT NULL;
ALTER TABLE ProductoTipo ADD CONSTRAINT fk_productotipo_tenant FOREIGN KEY (id_tenant) REFERENCES Tenant(id);
ALTER TABLE ProductoTipo DROP INDEX UKq6ghvuxhnh183bm7jsm8t1svr;
ALTER TABLE ProductoTipo ADD CONSTRAINT uk_productotipo_tenant_nombre UNIQUE (id_tenant, nombre);

ALTER TABLE Sesion ADD COLUMN id_tenant BIGINT NULL;
UPDATE Sesion SET id_tenant = 1;
ALTER TABLE Sesion MODIFY COLUMN id_tenant BIGINT NOT NULL;
ALTER TABLE Sesion ADD CONSTRAINT fk_sesion_tenant FOREIGN KEY (id_tenant) REFERENCES Tenant(id);

ALTER TABLE Usuario ADD COLUMN id_tenant BIGINT NULL;
UPDATE Usuario SET id_tenant = 1;
ALTER TABLE Usuario MODIFY COLUMN id_tenant BIGINT NOT NULL;
ALTER TABLE Usuario ADD CONSTRAINT fk_usuario_tenant FOREIGN KEY (id_tenant) REFERENCES Tenant(id);
ALTER TABLE Usuario ADD CONSTRAINT uk_usuario_tenant_username UNIQUE (id_tenant, nombre);
