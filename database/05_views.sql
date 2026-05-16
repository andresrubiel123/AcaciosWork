USE tienda_acacios;

/* =========================================================
VISTA: vista_stock_productos
Descripción:
Calcula el stock actual basado en movimientos
de inventario.

@author RADJ
========================================================= */

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
