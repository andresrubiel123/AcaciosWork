USE tienda_acacios;

/* =========================================================
ÍNDICES
Optimización de consultas y búsquedas
@author RADJ
========================================================= */


/* =========================================================
PRODUCTOS
========================================================= */

CREATE INDEX idx_producto_nombre
ON productos(nombre);

CREATE INDEX idx_producto_categoria
ON productos(id_categoria);

CREATE INDEX idx_producto_proveedor
ON productos(id_proveedor);


/* =========================================================
CLIENTES
========================================================= */

CREATE INDEX idx_cliente_nombre
ON clientes(nombre);


/* =========================================================
USUARIOS
========================================================= */

CREATE INDEX idx_usuario_nombre
ON usuarios(nombre);

CREATE INDEX idx_usuario_apellido
ON usuarios(apellido);

CREATE INDEX idx_usuario_rol
ON usuarios(id_rol);


/* =========================================================
PROVEEDORES
========================================================= */

CREATE INDEX idx_proveedor_nombre
ON proveedores(nombre);

CREATE INDEX idx_proveedor_tipo_documento
ON proveedores(id_tipo_documento);


/* =========================================================
VENTAS
========================================================= */

CREATE INDEX idx_venta_fecha
ON ventas(fecha_creacion);

CREATE INDEX idx_venta_usuario
ON ventas(id_usuario);

CREATE INDEX idx_venta_cliente
ON ventas(id_cliente);


/* =========================================================
COMPRAS
========================================================= */

CREATE INDEX idx_compra_fecha
ON compras(fecha_creacion);

CREATE INDEX idx_compra_usuario
ON compras(id_usuario);

CREATE INDEX idx_compra_proveedor
ON compras(id_proveedor);


/* =========================================================
MOVIMIENTOS INVENTARIO
========================================================= */

CREATE INDEX idx_movimiento_fecha
ON movimientos_inventario(fecha_creacion);

CREATE INDEX idx_movimiento_producto
ON movimientos_inventario(id_producto);

CREATE INDEX idx_movimiento_usuario
ON movimientos_inventario(id_usuario);

CREATE INDEX idx_movimiento_tipo
ON movimientos_inventario(tipo);


/* =========================================================
DETALLE VENTAS
========================================================= */

CREATE INDEX idx_detalle_venta
ON detalle_ventas(id_venta);

CREATE INDEX idx_detalle_venta_producto
ON detalle_ventas(id_producto);


/* =========================================================
DETALLE COMPRAS
========================================================= */

CREATE INDEX idx_detalle_compra
ON detalle_compras(id_compra);

CREATE INDEX idx_detalle_compra_producto
ON detalle_compras(id_producto);
