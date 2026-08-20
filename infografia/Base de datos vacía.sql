/* CreaciÃ³n de la base de datos tienda_acacios. @author RADJ */

DROP DATABASE IF EXISTS tienda_acacios;

CREATE DATABASE IF NOT EXISTS tienda_acacios
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE tienda_acacios;


USE tienda_acacios;

/* Tabla de roles del sistema. @author RADJ */

CREATE TABLE IF NOT EXISTS roles (

    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    nombre VARCHAR(100) NOT NULL,

    descripcion VARCHAR(255),

    activo TINYINT(1) NOT NULL DEFAULT 1,

    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    fecha_actualizacion TIMESTAMP NULL DEFAULT NULL
    ON UPDATE CURRENT_TIMESTAMP

) ENGINE=InnoDB;


/* Tabla de tipos de documentos. @author RADJ */

CREATE TABLE IF NOT EXISTS tipos_documentos (

    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    nombre VARCHAR(100) NOT NULL,

    activo TINYINT(1) NOT NULL DEFAULT 1,

    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    fecha_actualizacion TIMESTAMP NULL DEFAULT NULL
    ON UPDATE CURRENT_TIMESTAMP

) ENGINE=InnoDB;


/* Tabla de categorÃ­as de productos. @author RADJ */

CREATE TABLE IF NOT EXISTS categorias (

    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    nombre VARCHAR(100) NOT NULL,

    activo TINYINT(1) NOT NULL DEFAULT 1,

    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    fecha_actualizacion TIMESTAMP NULL DEFAULT NULL
    ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT uq_categoria_nombre
        UNIQUE (nombre)

) ENGINE=InnoDB;


/* Tabla de usuarios y credenciales. @author RADJ */

CREATE TABLE IF NOT EXISTS usuarios (

    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    id_tipo_documento BIGINT,

    numero_documento VARCHAR(20) NOT NULL,

    nombre VARCHAR(100) NOT NULL,

    apellido VARCHAR(100) NOT NULL,

    telefono VARCHAR(20),

    email VARCHAR(150) NOT NULL,

    usuario VARCHAR(100) NOT NULL,

    clave VARCHAR(255) NOT NULL,

    id_rol BIGINT NOT NULL,

    activo TINYINT(1) NOT NULL DEFAULT 1,

    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    fecha_actualizacion TIMESTAMP NULL DEFAULT NULL
    ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT uq_usuario_numero_documento
        UNIQUE (numero_documento),

    CONSTRAINT uq_usuario_email
        UNIQUE (email),

    CONSTRAINT uq_usuario_usuario
        UNIQUE (usuario),

    CONSTRAINT fk_usuario_rol
        FOREIGN KEY (id_rol)
        REFERENCES roles(id),

    CONSTRAINT fk_usuario_tipo_documento
        FOREIGN KEY (id_tipo_documento)
        REFERENCES tipos_documentos(id)

) ENGINE=InnoDB;


/* Tabla de clientes. @author RADJ */

CREATE TABLE IF NOT EXISTS clientes (

    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    id_tipo_documento BIGINT,

    numero_documento VARCHAR(20) NOT NULL,

    nombre VARCHAR(100) NOT NULL,

    telefono VARCHAR(20),

    email VARCHAR(150),

    direccion VARCHAR(200),

    frecuente TINYINT(1) NOT NULL DEFAULT 0,

    activo TINYINT(1) NOT NULL DEFAULT 1,

    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    fecha_actualizacion TIMESTAMP NULL DEFAULT NULL
    ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT uq_cliente_numero_documento
        UNIQUE (numero_documento),

    CONSTRAINT uq_cliente_email
        UNIQUE (email),

    CONSTRAINT fk_cliente_tipo_documento
        FOREIGN KEY (id_tipo_documento)
        REFERENCES tipos_documentos(id)

) ENGINE=InnoDB;


/* Tabla de proveedores. @author RADJ */

CREATE TABLE IF NOT EXISTS proveedores (

    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    id_tipo_documento BIGINT NOT NULL,

    numero_documento VARCHAR(20) NOT NULL,

    nombre VARCHAR(100) NOT NULL,

    telefono VARCHAR(20),

    email VARCHAR(150),

    direccion VARCHAR(200),

    cuenta_bancaria VARCHAR(100),

    activo TINYINT(1) NOT NULL DEFAULT 1,

    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    fecha_actualizacion TIMESTAMP NULL DEFAULT NULL
    ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT uq_proveedor_documento
        UNIQUE (numero_documento),

    CONSTRAINT uq_proveedor_email
        UNIQUE (email),

    CONSTRAINT fk_proveedor_tipo_documento
        FOREIGN KEY (id_tipo_documento)
        REFERENCES tipos_documentos(id)

) ENGINE=InnoDB;


/* Tabla de productos. @author RADJ */

CREATE TABLE IF NOT EXISTS productos (

    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    codigo_barras VARCHAR(100),

    nombre VARCHAR(150) NOT NULL,

    unidad_medida VARCHAR(150) NOT NULL,

    descripcion TEXT,

    stock_actual INT NOT NULL DEFAULT 0,

    stock_minimo INT NOT NULL DEFAULT 30,

    stock_optimo INT NOT NULL DEFAULT 200,

    fecha_vencimiento VARCHAR(20) NULL,

    precio_compra DECIMAL(12,2) NOT NULL DEFAULT 0.00,

    precio_venta DECIMAL(12,2) NOT NULL DEFAULT 0.00,

    iva DECIMAL(5,2) NOT NULL DEFAULT 0.00,

    activo TINYINT(1) NOT NULL DEFAULT 1,

    id_categoria BIGINT,

    id_proveedor BIGINT,

    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    fecha_actualizacion TIMESTAMP NULL DEFAULT NULL
    ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT uq_producto_codigo_barras
        UNIQUE (codigo_barras),

    CONSTRAINT fk_producto_categoria
        FOREIGN KEY (id_categoria)
        REFERENCES categorias(id),

    CONSTRAINT fk_producto_proveedor
        FOREIGN KEY (id_proveedor)
        REFERENCES proveedores(id)

) ENGINE=InnoDB;


/* Tabla de movimientos de inventario. @author RADJ */

CREATE TABLE IF NOT EXISTS movimientos_inventario (

    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    id_producto BIGINT NOT NULL,

    tipo ENUM(
        'ENTRADA',
        'SALIDA',
        'AJUSTE'
    ) NOT NULL,

    cantidad INT NOT NULL,

    referencia VARCHAR(100),

    observacion VARCHAR(255),

    id_usuario BIGINT,

    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    fecha_actualizacion TIMESTAMP NULL DEFAULT NULL
    ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_movimiento_producto
        FOREIGN KEY (id_producto)
        REFERENCES productos(id),

    CONSTRAINT fk_movimiento_usuario
        FOREIGN KEY (id_usuario)
        REFERENCES usuarios(id)

) ENGINE=InnoDB;


/* Tabla de Ã³rdenes de compra. @author RADJ */

CREATE TABLE IF NOT EXISTS compras (

    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    id_proveedor BIGINT NOT NULL,

    id_usuario BIGINT NOT NULL,

    total DECIMAL(12,2) NOT NULL,

    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    fecha_actualizacion TIMESTAMP NULL DEFAULT NULL
    ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_compra_proveedor
        FOREIGN KEY (id_proveedor)
        REFERENCES proveedores(id),

    CONSTRAINT fk_compra_usuario
        FOREIGN KEY (id_usuario)
        REFERENCES usuarios(id)

) ENGINE=InnoDB;


/* Tabla de detalle de compras. @author RADJ */

CREATE TABLE IF NOT EXISTS detalle_compras (

    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    id_compra BIGINT NOT NULL,

    id_producto BIGINT NOT NULL,

    cantidad INT NOT NULL,

    costo_unitario DECIMAL(12,2) NOT NULL,

    subtotal DECIMAL(12,2) NOT NULL,

    CONSTRAINT fk_detalle_compra
        FOREIGN KEY (id_compra)
        REFERENCES compras(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_detalle_compra_producto
        FOREIGN KEY (id_producto)
        REFERENCES productos(id)

) ENGINE=InnoDB;


/* Tabla de registros de ventas. @author RADJ */

CREATE TABLE IF NOT EXISTS ventas (

    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    valor_total DECIMAL(12,2) NOT NULL,

    id_usuario BIGINT NOT NULL,

    id_cliente BIGINT,

    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    fecha_actualizacion TIMESTAMP NULL DEFAULT NULL
    ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_venta_usuario
        FOREIGN KEY (id_usuario)
        REFERENCES usuarios(id),

    CONSTRAINT fk_venta_cliente
        FOREIGN KEY (id_cliente)
        REFERENCES clientes(id)

) ENGINE=InnoDB;


/* Tabla de detalle de ventas. @author RADJ */

CREATE TABLE IF NOT EXISTS detalle_ventas (

    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    id_venta BIGINT NOT NULL,

    id_producto BIGINT NOT NULL,

    cantidad INT NOT NULL,

    precio_unitario DECIMAL(12,2) NOT NULL,

    subtotal DECIMAL(12,2) NOT NULL,

    CONSTRAINT fk_detalle_venta
        FOREIGN KEY (id_venta)
        REFERENCES ventas(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_detalle_venta_producto
        FOREIGN KEY (id_producto)
        REFERENCES productos(id)

) ENGINE=InnoDB;


/* Tabla de lotes de productos para fechas de vencimiento. @author RADJ */

CREATE TABLE IF NOT EXISTS lotes (

    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    id_producto BIGINT NOT NULL,

    codigo_lote VARCHAR(100),

    cantidad_inicial INT NOT NULL,

    cantidad_actual INT NOT NULL,

    fecha_ingreso TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    fecha_vencimiento VARCHAR(20) NOT NULL,

    activo TINYINT(1) NOT NULL DEFAULT 1,

    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    fecha_actualizacion TIMESTAMP NULL DEFAULT NULL
    ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_lote_producto
        FOREIGN KEY (id_producto)
        REFERENCES productos(id)

) ENGINE=InnoDB;



USE tienda_acacios;

/* Ãndices para optimizaciÃ³n de consultas. @author RADJ */


/* Ãndices para la tabla de productos. @author RADJ */

CREATE INDEX idx_producto_nombre
ON productos(nombre);

CREATE INDEX idx_producto_categoria
ON productos(id_categoria);

CREATE INDEX idx_producto_proveedor
ON productos(id_proveedor);


/* Ãndices para la tabla de clientes. @author RADJ */

CREATE INDEX idx_cliente_nombre
ON clientes(nombre);


/* Ãndices para la tabla de usuarios. @author RADJ */

CREATE INDEX idx_usuario_nombre
ON usuarios(nombre);

CREATE INDEX idx_usuario_apellido
ON usuarios(apellido);

CREATE INDEX idx_usuario_rol
ON usuarios(id_rol);


/* Ãndices para la tabla de proveedores. @author RADJ */

CREATE INDEX idx_proveedor_nombre
ON proveedores(nombre);

CREATE INDEX idx_proveedor_tipo_documento
ON proveedores(id_tipo_documento);


/* Ãndices para la tabla de ventas. @author RADJ */

CREATE INDEX idx_venta_fecha
ON ventas(fecha_creacion);

CREATE INDEX idx_venta_usuario
ON ventas(id_usuario);

CREATE INDEX idx_venta_cliente
ON ventas(id_cliente);


/* Ãndices para la tabla de compras. @author RADJ */

CREATE INDEX idx_compra_fecha
ON compras(fecha_creacion);

CREATE INDEX idx_compra_usuario
ON compras(id_usuario);

CREATE INDEX idx_compra_proveedor
ON compras(id_proveedor);


/* Ãndices para la tabla de movimientos de inventario. @author RADJ */

CREATE INDEX idx_movimiento_fecha
ON movimientos_inventario(fecha_creacion);

CREATE INDEX idx_movimiento_producto
ON movimientos_inventario(id_producto);

CREATE INDEX idx_movimiento_usuario
ON movimientos_inventario(id_usuario);

CREATE INDEX idx_movimiento_tipo
ON movimientos_inventario(tipo);


/* Ãndices para la tabla de detalle de ventas. @author RADJ */

CREATE INDEX idx_detalle_venta
ON detalle_ventas(id_venta);

CREATE INDEX idx_detalle_venta_producto
ON detalle_ventas(id_producto);


/* Ãndices para la tabla de detalle de compras. @author RADJ */

CREATE INDEX idx_detalle_compra
ON detalle_compras(id_compra);

CREATE INDEX idx_detalle_compra_producto
ON detalle_compras(id_producto);


USE tienda_acacios;

/* Vista para el cÃ¡lculo del stock actual de productos. @author RADJ */

CREATE VIEW vista_stock_productos AS

SELECT

    p.id,
    p.nombre,

    COALESCE(

        SUM(

            CASE

                WHEN m.tipo = 'ENTRADA'
                THEN m.cantidad

                WHEN m.tipo = 'SALIDA'
                THEN -m.cantidad

                WHEN m.tipo = 'AJUSTE'
                THEN m.cantidad

            END

        ), 0

    ) AS stock_actual

FROM productos p

LEFT JOIN movimientos_inventario m
    ON p.id = m.id_producto

GROUP BY p.id, p.nombre;


USE tienda_acacios;

/* Triggers para automatizaciÃ³n y validaciones del sistema. @author RADJ */

DELIMITER $$


/* Valida cantidad positiva en movimientos. @author RADJ */

CREATE TRIGGER trg_validar_cantidad_movimiento

BEFORE INSERT
ON movimientos_inventario

FOR EACH ROW

BEGIN

    IF NEW.cantidad <= 0 THEN

        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT =
        'La cantidad debe ser mayor a cero';

    END IF;

END$$



/* Valida precio de compra positivo. @author RADJ */

CREATE TRIGGER trg_validar_precio_compra

BEFORE INSERT
ON productos

FOR EACH ROW

BEGIN

    IF NEW.precio_compra < 0 THEN

        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT =
        'El precio de compra no puede ser negativo';

    END IF;

END$$



/* Valida precio de venta positivo. @author RADJ */

CREATE TRIGGER trg_validar_precio_venta

BEFORE INSERT
ON productos

FOR EACH ROW

BEGIN

    IF NEW.precio_venta < 0 THEN

        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT =
        'El precio de venta no puede ser negativo';

    END IF;

END$$



/* Valida IVA correcto. @author RADJ */

CREATE TRIGGER trg_validar_iva

BEFORE INSERT
ON productos

FOR EACH ROW

BEGIN

    IF NEW.iva < 0 THEN

        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT =
        'El IVA no puede ser negativo';

    END IF;

END$$



/* Valida stock mÃ­nimo positivo. @author RADJ */

CREATE TRIGGER trg_validar_stock_minimo

BEFORE INSERT
ON productos

FOR EACH ROW

BEGIN

    IF NEW.stock_minimo < 0 THEN

        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT =
        'El stock mÃ­nimo no puede ser negativo';

    END IF;

END$$



/* Valida subtotal en detalle de ventas. @author RADJ */

CREATE TRIGGER trg_validar_subtotal_venta

BEFORE INSERT
ON detalle_ventas

FOR EACH ROW

BEGIN

    IF NEW.subtotal <= 0 THEN

        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT =
        'El subtotal de venta debe ser mayor a cero';

    END IF;

END$$



/* Valida subtotal en detalle de compras. @author RADJ */

CREATE TRIGGER trg_validar_subtotal_compra

BEFORE INSERT
ON detalle_compras

FOR EACH ROW

BEGIN

    IF NEW.subtotal <= 0 THEN

        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT =
        'El subtotal de compra debe ser mayor a cero';

    END IF;

END$$



/* Evita ventas sin productos (cantidad > 0). @author RADJ */

CREATE TRIGGER trg_validar_cantidad_venta

BEFORE INSERT
ON detalle_ventas

FOR EACH ROW

BEGIN

    IF NEW.cantidad <= 0 THEN

        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT =
        'La cantidad vendida debe ser mayor a cero';

    END IF;

END$$



/* Evita compras sin productos (cantidad > 0). @author RADJ */

CREATE TRIGGER trg_validar_cantidad_compra

BEFORE INSERT
ON detalle_compras

FOR EACH ROW

BEGIN

    IF NEW.cantidad <= 0 THEN

        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT =
        'La cantidad comprada debe ser mayor a cero';

    END IF;

END$$



/* Valida formato de correo de usuario. @author RADJ */

CREATE TRIGGER trg_validar_email_usuario

BEFORE INSERT
ON usuarios

FOR EACH ROW

BEGIN

    IF NEW.email NOT LIKE '%@%' THEN

        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT =
        'Correo electrÃ³nico invÃ¡lido';

    END IF;

END$$



/* Valida formato de correo de proveedor. @author RADJ */

CREATE TRIGGER trg_validar_email_proveedor

BEFORE INSERT
ON proveedores

FOR EACH ROW

BEGIN

    IF NEW.email IS NOT NULL
    AND NEW.email NOT LIKE '%@%' THEN

        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT =
        'Correo del proveedor invÃ¡lido';

    END IF;

END$$



/* Valida formato de correo de cliente. @author RADJ */

CREATE TRIGGER trg_validar_email_cliente

BEFORE INSERT
ON clientes

FOR EACH ROW

BEGIN

    IF NEW.email IS NOT NULL
    AND NEW.email NOT LIKE '%@%' THEN

        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT =
        'Correo del cliente invÃ¡lido';

    END IF;

END$$

DELIMITER ;
