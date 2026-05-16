USE tienda_acacios;

/* =========================================================
TRIGGERS
Automatización y validaciones del sistema
@author RADJ
========================================================= */

DELIMITER $$


/* =========================================================
TRIGGER:
Validar cantidad positiva en movimientos
========================================================= */
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



/* =========================================================
TRIGGER:
Validar precio compra positivo
========================================================= */

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



/* =========================================================
TRIGGER:
Validar precio venta positivo
========================================================= */

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



/* =========================================================
TRIGGER:
Validar IVA correcto
========================================================= */

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



/* =========================================================
TRIGGER:
Validar stock mínimo
========================================================= */

CREATE TRIGGER trg_validar_stock_minimo

BEFORE INSERT
ON productos

FOR EACH ROW

BEGIN

    IF NEW.stock_minimo < 0 THEN

        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT =
        'El stock mínimo no puede ser negativo';

    END IF;

END$$



/* =========================================================
TRIGGER:
Validar subtotal detalle ventas
========================================================= */

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



/* =========================================================
TRIGGER:
Validar subtotal detalle compras
========================================================= */

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



/* =========================================================
TRIGGER:
Evitar ventas sin productos
========================================================= */

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



/* =========================================================
TRIGGER:
Evitar compras sin productos
========================================================= */

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



/* =========================================================
TRIGGER:
Validar correo usuario
========================================================= */

CREATE TRIGGER trg_validar_email_usuario

BEFORE INSERT
ON usuarios

FOR EACH ROW

BEGIN

    IF NEW.email NOT LIKE '%@%' THEN

        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT =
        'Correo electrónico inválido';

    END IF;

END$$



/* =========================================================
TRIGGER:
Validar correo proveedor
========================================================= */

CREATE TRIGGER trg_validar_email_proveedor

BEFORE INSERT
ON proveedores

FOR EACH ROW

BEGIN

    IF NEW.email IS NOT NULL
    AND NEW.email NOT LIKE '%@%' THEN

        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT =
        'Correo del proveedor inválido';

    END IF;

END$$



/* =========================================================
TRIGGER:
Validar correo cliente
========================================================= */

CREATE TRIGGER trg_validar_email_cliente

BEFORE INSERT
ON clientes

FOR EACH ROW

BEGIN

    IF NEW.email IS NOT NULL
    AND NEW.email NOT LIKE '%@%' THEN

        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT =
        'Correo del cliente inválido';

    END IF;

END$$

DELIMITER ;tienda_acacios