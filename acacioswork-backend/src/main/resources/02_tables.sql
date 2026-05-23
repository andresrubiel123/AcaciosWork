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


/* Tabla de categorías de productos. @author RADJ */

CREATE TABLE IF NOT EXISTS categorias (

    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    nombre VARCHAR(100) NOT NULL,

    activo TINYINT(1) NOT NULL DEFAULT 1,

    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    fecha_actualizacion TIMESTAMP NULL DEFAULT NULL
    ON UPDATE CURRENT_TIMESTAMP

    CONSTRAINT uq_categoria_nombre
        UNIQUE (nombre)

) ENGINE=InnoDB;


/* Tabla de usuarios y credenciales. @author RADJ */

CREATE TABLE IF NOT EXISTS usuarios (

    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    identificacion VARCHAR(20) NOT NULL,

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

    CONSTRAINT uq_usuario_identificacion
        UNIQUE (identificacion),

    CONSTRAINT uq_usuario_email
        UNIQUE (email),

    CONSTRAINT uq_usuario_usuario
        UNIQUE (usuario),

    CONSTRAINT fk_usuario_rol
        FOREIGN KEY (id_rol)
        REFERENCES roles(id)

) ENGINE=InnoDB;


/* Tabla de clientes. @author RADJ */

CREATE TABLE IF NOT EXISTS clientes (

    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    identificacion VARCHAR(20) NOT NULL,

    nombre VARCHAR(100) NOT NULL,

    telefono VARCHAR(20),

    email VARCHAR(150),

    direccion VARCHAR(200),

    frecuente TINYINT(1) NOT NULL DEFAULT 0,

    activo TINYINT(1) NOT NULL DEFAULT 1,

    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    fecha_actualizacion TIMESTAMP NULL DEFAULT NULL
    ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT uq_cliente_identificacion
        UNIQUE (identificacion),

    CONSTRAINT uq_cliente_email
        UNIQUE (email)

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


/* Tabla de órdenes de compra. @author RADJ */

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
