USE tienda_acacios;

/* Índices para optimización de consultas. @author RADJ */


/* Índices para la tabla de productos. @author RADJ */

CREATE INDEX idx_producto_nombre
ON productos(nombre);

CREATE INDEX idx_producto_categoria
ON productos(id_categoria);

CREATE INDEX idx_producto_proveedor
ON productos(id_proveedor);


/* Índices para la tabla de clientes. @author RADJ */

CREATE INDEX idx_cliente_nombre
ON clientes(nombre);


/* Índices para la tabla de usuarios. @author RADJ */

CREATE INDEX idx_usuario_nombre
ON usuarios(nombre);

CREATE INDEX idx_usuario_apellido
ON usuarios(apellido);

CREATE INDEX idx_usuario_rol
ON usuarios(id_rol);


/* Índices para la tabla de proveedores. @author RADJ */

CREATE INDEX idx_proveedor_nombre
ON proveedores(nombre);

CREATE INDEX idx_proveedor_tipo_documento
ON proveedores(id_tipo_documento);


/* Índices para la tabla de ventas. @author RADJ */

CREATE INDEX idx_venta_fecha
ON ventas(fecha_creacion);

CREATE INDEX idx_venta_usuario
ON ventas(id_usuario);

CREATE INDEX idx_venta_cliente
ON ventas(id_cliente);


/* Índices para la tabla de compras. @author RADJ */

CREATE INDEX idx_compra_fecha
ON compras(fecha_creacion);

CREATE INDEX idx_compra_usuario
ON compras(id_usuario);

CREATE INDEX idx_compra_proveedor
ON compras(id_proveedor);


/* Índices para la tabla de movimientos de inventario. @author RADJ */

CREATE INDEX idx_movimiento_fecha
ON movimientos_inventario(fecha_creacion);

CREATE INDEX idx_movimiento_producto
ON movimientos_inventario(id_producto);

CREATE INDEX idx_movimiento_usuario
ON movimientos_inventario(id_usuario);

CREATE INDEX idx_movimiento_tipo
ON movimientos_inventario(tipo);


/* Índices para la tabla de detalle de ventas. @author RADJ */

CREATE INDEX idx_detalle_venta
ON detalle_ventas(id_venta);

CREATE INDEX idx_detalle_venta_producto
ON detalle_ventas(id_producto);


/* Índices para la tabla de detalle de compras. @author RADJ */

CREATE INDEX idx_detalle_compra
ON detalle_compras(id_compra);

CREATE INDEX idx_detalle_compra_producto
ON detalle_compras(id_producto);
